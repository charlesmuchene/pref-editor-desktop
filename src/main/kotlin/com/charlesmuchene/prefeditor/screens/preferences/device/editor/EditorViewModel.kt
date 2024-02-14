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

package com.charlesmuchene.prefeditor.screens.preferences.device.editor

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.models.PreferenceType
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferenceValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.jetbrains.jewel.ui.Outline
import java.util.*
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

sealed interface PreferenceState {
    data object Changed : PreferenceState
    data object Deleted : PreferenceState
    data object None : PreferenceState
    data object New : PreferenceState
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
    private val validator = PreferenceValidator()
    private val changes = MutableSharedFlow<CharSequence>()

    private lateinit var initialPrefs: Map<String, String>
    private lateinit var edits: MutableMap<String, UIPreference>

    val backupEnabled = prefUseCase.backup

    private val _preferences = mutableStateOf(emptyList<UIPreference>())
    val preferences: State<List<UIPreference>> = _preferences

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> = changes
        .map { validator.allowedEdits(edits = edits) }
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = false)

    init {
        prefUseCase.preferences.onEach { prefs ->
            initialPrefs = prefs.preferences.associate(Preference::toPair)
            edits = prefs.preferences.map(::UIPreference).associateBy { it.preference.name }.toMutableMap()
            _preferences.value = edits.values.toList()
        }.launchIn(scope = scope)
    }

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
        if (output.all { it.isSuccess }) appState.showToast("Changes saved to ${prefFile.name}")
        else output.filter { it.isFailure }.map { logger.error(it.exceptionOrNull()) {} }
    }

    /**
     * Enable or disable preference file backup before edit
     *
     * @param backup `true` to back up file, `false` otherwise
     */
    fun backup(backup: Boolean) {
        prefUseCase.backup.value = backup
    }

    /**
     * Determine preference outline
     *
     * @param preference Edited [Preference]
     * @return UI [Outline] to apply
     */
    fun outline(preference: Preference): Outline = when (preference) {
        is FloatPreference, is IntPreference, is LongPreference -> numberOutline(preference = preference)
        is BooleanPreference, is StringPreference -> if (initialPrefs[preference.name] == preference.value) Outline.None else Outline.Warning
        else -> Outline.None
    }

    /**
     * Determine outline for a number preference
     *
     * @param preference Number [Preference]: Int, Long, Float
     * @return [Outline] instance
     */
    private fun numberOutline(preference: Preference): Outline {
        if (initialPrefs[preference.name] == preference.value) return Outline.None
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
        val value = initialPrefs[preference.name] ?: error("Missing preference value ${preference.name}")
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
        val state =
            if (initialPrefs[preference.name] == newPref.value) PreferenceState.None else PreferenceState.Changed
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

    /**
     * Add a new preference
     *
     * @param name Preference name
     * @param value Preference value
     * @param type [PreferenceType] instance
     */
    suspend fun add(name: String, value: String, type: PreferenceType?): Boolean = coroutineScope {
        if (type == null) run {
            _message.emit("Select a data type")
            return@coroutineScope false
        }

        val preference = createPreference(name = name, value = value, type = type)
        if (validator.isValid(preference)) {
            prefUseCase.addPreference(preference).run { isSuccess }
        } else {
            _message.emit("'$value' cannot be added as ${type.name}")
            false
        }
    }

    private fun createPreference(name: String, value: String, type: PreferenceType): Preference = when (type) {
        PreferenceType.Boolean -> BooleanPreference(name = name, value = value)
        PreferenceType.String -> StringPreference(name = name, value = value)
        PreferenceType.Float -> FloatPreference(name = name, value = value)
        PreferenceType.Integer -> IntPreference(name = name, value = value)
        PreferenceType.Long -> LongPreference(name = name, value = value)
        PreferenceType.Set -> SetPreference(name = name, entries = listOf(value))
    }
}