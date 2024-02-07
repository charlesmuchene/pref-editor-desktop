/*
 * Copyright (c) 2024 Charles Muchene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charlesmuchene.prefeditor.screens.preferences.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.SetSubPreference
import com.charlesmuchene.prefeditor.validation.PreferenceValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.jewel.ui.Outline
import java.util.*
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

sealed interface PreferenceState {
    data object Changed : PreferenceState
    data object Deleted : PreferenceState
    data object None : PreferenceState
}

data class UIPreference(val preference: Preference, val state: PreferenceState = PreferenceState.None)

sealed interface PreferenceAction {
    data class Reset(val preference: Preference) : PreferenceAction
    data class Delete(val preference: Preference) : PreferenceAction
    data class Change(val preference: Preference, val change: String) : PreferenceAction
}


/**
 * Models data for the [Editor]
 */
class EditorViewModel(
    private val appState: AppState,
    private val scope: CoroutineScope,
    private val prefUseCase: DevicePreferencesUseCase,
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by scope + context {

    private val prefFile = prefUseCase.file
    private val originalPrefs = prefUseCase.preferences.value.preferences
    private val validator: PreferenceValidator = PreferenceValidator(original = originalPrefs)
    private var enableBackup = false
    private val _message = MutableSharedFlow<String?>()
    private val changes = MutableSharedFlow<CharSequence>()
    private val original = originalPrefs.associate(Preference::toPair)

    private val edits = originalPrefs.map(::UIPreference).associateBy { it.preference.name }.toMutableMap()
    private val _preferences = mutableStateOf(edits.values.toList())
    val preferences: State<List<UIPreference>> = _preferences
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> = changes
        .map { validator.allowedEdits(edits = edits) }
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = false)

    /**
     * Create sub-preferences for a set preference
     *
     * @param preference [SetPreference]
     * @return [List] of [SetSubPreference.Preference]
     */
    fun createSubPreferences(preference: SetPreference): Pair<SetSubPreference.Header, List<SetSubPreference.Preference>> =
        Pair(SetSubPreference.Header(preference.name), preference.entries.map(SetSubPreference::Preference))

    /**
     * Save edits
     */
    fun save() {
        launch {
            val prefs = edits.values.filter { it.state == PreferenceState.Changed }.map(UIPreference::preference)
            val isValid = validator.valid(prefs)
            if (isValid) saveChangesNow() else showInvalidEdits()
        }
        logger.debug { "$edits" }
    }

    /**
     * Notify the developer of invalid edits
     */
    private fun showInvalidEdits() {
        val message = "Invalid edits"
        launch { _message.emit(message) }
    }

    private suspend fun saveChangesNow() {
        val output = prefUseCase.writePreferences(edits.values)
        if (output.isNotBlank()) logger.info { "Saved Changes: $output" }
        appState.showToast("Changes saved to ${prefFile.name}")
    }

    /**
     * Enable or disable preference file backup
     *
     * @param backup `true` to back up file, `false` otherwise
     */
    fun backup(backup: Boolean) {
        enableBackup = backup
    }

    /**
     * Determine preference outline
     *
     * @param preference Edited [Preference]
     * @return UI [Outline] to apply
     */
    fun outline(preference: Preference): Outline = when (preference) {
        is FloatPreference, is IntPreference, is LongPreference -> numberOutline(preference = preference)
        is BooleanPreference, is StringPreference -> if (original[preference.name] == preference.value) Outline.None else Outline.Warning
        else -> Outline.None
    }

    /**
     * Determine outline for a number preference
     *
     * @param preference Number [Preference]: Int, Long, Float
     * @return [Outline] instance
     */
    private fun numberOutline(preference: Preference): Outline {
        if (original[preference.name] == preference.value) return Outline.None
        return if (validator.isValid(preference = preference)) Outline.Warning else Outline.Error
    }

    /**
     * Preference action
     *
     * @param action Developer [PreferenceAction]
     * @return Resulting [UIPreference] instance
     */
    fun preferenceAction(action: PreferenceAction): UIPreference = when (action) {
        is PreferenceAction.Change -> preferenceChanged(action.preference, action.change)
        is PreferenceAction.Delete -> preferenceDeleted(action.preference)
        is PreferenceAction.Reset -> preferenceReset(action.preference)
    }

    /**
     * Preference reset
     *
     * @param preference Reset [Preference]
     * @return Unedited [UIPreference]
     */
    private fun preferenceReset(preference: Preference): UIPreference {
        val value = original[preference.name] ?: error("Missing preference value ${preference.name}")
        return UIPreference(createPreference(preference = preference, value = value)).also { uiPreference ->
            edits[preference.name] = uiPreference
            launch { changes.emit(UUID.randomUUID().toString()) }
        }
    }

    /**
     * Preference deleted
     *
     * @param preference Deleted [Preference]
     * @return [UIPreference] with [PreferenceState.Deleted] state
     */
    private fun preferenceDeleted(preference: Preference): UIPreference {
        return UIPreference(preference = preference, state = PreferenceState.Deleted).also { uiPreference ->
            edits[preference.name] = uiPreference
            launch { changes.emit(UUID.randomUUID().toString()) }
        }
    }

    /**
     * Preference content changed
     *
     * @param preference Displayed [Preference]
     * @param change [Preference] content change
     * @return [UIPreference] with the [PreferenceState.Changed] if [preference] differs from original,
     * [PreferenceState.None] otherwise
     */
    private fun preferenceChanged(preference: Preference, change: String): UIPreference {
        val newPref = createPreference(preference = preference, value = change)
        val state = if (original[preference.name] == newPref.value) PreferenceState.None else PreferenceState.Changed
        return UIPreference(preference = newPref, state = state).also { uiPreference ->
            edits[preference.name] = uiPreference
            launch { changes.emit(change) }
        }
    }

    /**
     * Create a preference during edits
     *
     * @param preference Existing [Preference] to evaluate type
     * @param value Preference value
     * @return The created [Preference]
     */
    private fun createPreference(preference: Preference, value: String) = when (preference) {
        is BooleanPreference -> BooleanPreference(preference.name, value = value)
        is FloatPreference -> FloatPreference(preference.name, value = value)
        is IntPreference -> IntPreference(preference.name, value = value)
        is LongPreference -> LongPreference(preference.name, value = value)
        is StringPreference -> StringPreference(preference.name, value = value)
        else -> error("Changing $preference is not supported")
    }
}