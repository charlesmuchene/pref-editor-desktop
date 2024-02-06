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
import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.command.WritePref
import com.charlesmuchene.prefeditor.command.editor.DeviceEditorCommand
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefListScreen
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.SetSubPreference
import com.charlesmuchene.prefeditor.validation.PreferenceValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.jewel.ui.Outline
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
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val appState: AppState,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val preferences: Preferences,
    private val context: CoroutineContext = Dispatchers.Default,
    private val validator: PreferenceValidator = PreferenceValidator(original = preferences.preferences),
) : CoroutineScope by scope + context {

    private val codec = DevicePreferencesCodec(codec = PreferencesCodec())
    private val editorCommand = DeviceEditorCommand(app = app, device = device, file = prefFile)
    private val editor = PreferenceEditor(command = editorCommand)
    private val useCase = DevicePreferencesUseCase(codec = codec, editor = editor)

    private var enableBackup = false
    private val _message = MutableSharedFlow<String?>()
    private val changes = MutableSharedFlow<CharSequence>()
    private val edits = preferences.preferences.associate(Preference::toPair).toMutableMap()

    private val original = preferences.preferences.associate { it.name to it.value }

    // TODO Rename to edits
    private val edited = preferences.preferences.map(::UIPreference).associateBy { it.preference.name }.toMutableMap()
    private val _preferences = mutableStateOf(edited.values.toList())
    val entries: State<List<UIPreference>> = _preferences
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> = changes
        .map { validator.validEdits(edits) }
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = true)

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
            val prefs = edited.values.filter { it.state == PreferenceState.Changed }.map(UIPreference::preference)
            val isValid = validator.valid(prefs)
            if (isValid) saveChangesNow() else showInvalidEdits()
        }
        logger.debug { "$edited" }
    }

    /**
     * Notify the developer of invalid edits
     */
    private fun showInvalidEdits() {
        val message = "Invalid edits"
        launch { _message.emit(message) }
        logger.info { "$message: $edits" }
    }

    private suspend fun saveChangesNow() {
        val output = useCase.writePreferences(edited.values)
        logger.info { "Saved Changes: $output" }
    }

    private suspend fun saveChanges() {
        val preferences = validator.editsToPreferences(edits = edits)
        val command = WritePref(
            app = app,
            device = device,
            prefFile = prefFile,
            preferences = preferences,
            enableBackup = enableBackup,
        )
        val result = bridge.execute(command)
        when {
            result.isSuccess -> {
                appState.showToast("'${prefFile.name}' saved successfully!")
                navigation.navigate(screen = PrefListScreen(app = app, device = device))
            }

            else -> {
                val message = "Error saving preferences"
                _message.emit(message)
                logger.error(result.exceptionOrNull()) { message }
            }
        }
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
        return UIPreference(createPreference(preference = preference, value = value)).also {
            edited[preference.name] = it
        }
    }

    /**
     * Preference deleted
     *
     * @param preference Deleted [Preference]
     * @return [UIPreference] with [PreferenceState.Deleted] state
     */
    private fun preferenceDeleted(preference: Preference): UIPreference {
        return UIPreference(preference = preference, state = PreferenceState.Deleted).also {
            edited[preference.name] = it
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
//        launch { changes.emit(change) }

        val newPref = createPreference(preference = preference, value = change)
        val state = if (original[preference.name] == newPref.value) PreferenceState.None else PreferenceState.Changed
        return UIPreference(preference = newPref, state = state).also { edited[preference.name] = it }
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