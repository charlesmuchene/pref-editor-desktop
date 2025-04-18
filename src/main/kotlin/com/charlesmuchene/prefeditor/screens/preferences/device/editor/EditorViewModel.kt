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

import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.datastore.preferences.ByteArrayPreference
import com.charlesmuchene.datastore.preferences.DoublePreference
import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.datastore.preferences.StringSetPreference
import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.models.PreferenceType
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferenceValidator
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.SetSubPreference.Header
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.jewel.ui.Outline
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger { }

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
@Suppress("TooManyFunctions")
class EditorViewModel(
    private val appState: AppState,
    private val scope: CoroutineScope,
    private val prefUseCase: DevicePreferencesUseCase,
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by scope + context {
    private val prefFile = prefUseCase.file
    private val validator = PreferenceValidator()
    private val changes = MutableSharedFlow<Unit>()

    private lateinit var initialPrefs: Map<String, String>
    private lateinit var edits: MutableMap<String, UIPreference>

    val backupEnabled = prefUseCase.backup

    private val _preferences = MutableStateFlow(emptyList<UIPreference>())
    val preferences: StateFlow<List<UIPreference>> = _preferences

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> =
        changes
            .map { validator.hasEdits(edits = edits) }
            .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = false)

    init {
        scope.launch {
            prefUseCase.preferences
                .map { preferences ->
                    val prefs = preferences.prefs()
                    initialPrefs = prefs.associate(Preference::toPair)
                    prefs.map(::UIPreference).also { map ->
                        edits = map.associateBy { it.preference.key }.toMutableMap()
                    }
                }
                .collect(_preferences)
        }
    }

    /**
     * Create sub-preferences for a set preference
     *
     * @param preference [StringSetPreference]
     * @return [List] of [SetSubPreference.Pref]
     */
    fun createSubPreferences(preference: StringSetPreference): Pair<Header, List<SetSubPreference.Pref>> =
        Pair(Header(preference.key), preference.entries.map(SetSubPreference::Pref))

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
        if (prefUseCase.writePreferences(edits.values)) {
            appState.showToast("Changes saved to ${prefFile.name}")
        } else {
            _message.emit("Error saving changes. Check log for details.")
        }
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
    fun outline(preference: Preference): Outline =
        when (preference) {
            is FloatPreference, is IntPreference, is LongPreference -> numberOutline(preference = preference)
            is BooleanPreference, is StringPreference ->
                if (initialPrefs[preference.key] == preference.value) Outline.None else Outline.Warning

            else -> Outline.None
        }

    /**
     * Determine outline for a number preference
     *
     * @param preference Number [Preference]: Int, Long, Float
     * @return [Outline] instance
     */
    private fun numberOutline(preference: Preference): Outline {
        if (initialPrefs[preference.key] == preference.value) return Outline.None
        return if (validator.isValid(preference = preference)) Outline.Warning else Outline.Error
    }

    /**
     * Preference action
     *
     * @param action Developer [PreferenceActionButtonRow]
     * @return Resulting [UIPreference] instance
     */
    fun preferenceAction(action: PreferenceAction): UIPreference =
        when (action) {
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
        val value = initialPrefs[preference.key] ?: error("Missing preference value ${preference.key}")
        return UIPreference(createPreference(preference = preference, value = value)).also { uiPreference ->
            edits[preference.key] = uiPreference
            launch { changes.emit(Unit) }
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
            edits[preference.key] = uiPreference
            launch { changes.emit(Unit) }
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
    private fun preferenceChanged(
        preference: Preference,
        change: String,
    ): UIPreference {
        val newPref = createPreference(preference = preference, value = change)
        val state =
            if (initialPrefs[preference.key] == newPref.value) PreferenceState.None else PreferenceState.Changed
        return UIPreference(preference = newPref, state = state).also { uiPreference ->
            edits[preference.key] = uiPreference
            launch { changes.emit(Unit) }
        }
    }

    /**
     * Create a preference during edits
     *
     * @param preference Existing [Preference] to evaluate type
     * @param value Preference value
     * @return The created [Preference]
     */
    private fun createPreference(
        preference: Preference,
        value: String,
    ) = when (preference) {
        is BooleanPreference -> BooleanPreference(preference.key, value = value)
        is FloatPreference -> FloatPreference(preference.key, value = value)
        is IntPreference -> IntPreference(preference.key, value = value)
        is LongPreference -> LongPreference(preference.key, value = value)
        is StringPreference -> StringPreference(preference.key, value = value)
        else -> error("Changing $preference is not supported")
    }

    /**
     * Add a new preference
     *
     * @param name Preference name
     * @param value Preference value
     * @param type [PreferenceType] instance
     */
    suspend fun add(
        name: String,
        value: String,
        type: PreferenceType?,
    ): Boolean =
        coroutineScope {
            if (type == null) {
                run {
                    _message.emit("Select a data type")
                    return@coroutineScope false
                }
            }

            val preference = createPreference(name = name, value = value, type = type)
            if (validator.isValid(preference)) {
                val result = prefUseCase.addPreference(preference)
                if (!result) _message.emit("Error adding $value.")
                result
            } else {
                _message.emit("'$value' cannot be added as ${type.name}")
                false
            }
        }

    private fun createPreference(
        name: String,
        value: String,
        type: PreferenceType,
    ): Preference =
        when (type) {
            PreferenceType.Boolean -> BooleanPreference(key = name, value = value)
            PreferenceType.String -> StringPreference(key = name, value = value)
            PreferenceType.Float -> FloatPreference(key = name, value = value)
            PreferenceType.Integer -> IntPreference(key = name, value = value)
            PreferenceType.Long -> LongPreference(key = name, value = value)
            PreferenceType.Double -> DoublePreference(key = name, value = value)
            PreferenceType.StringSet -> StringSetPreference(key = name, entries = setOf(value))
            PreferenceType.ByteArray -> ByteArrayPreference(key = name, content = value.toByteArray())
        }
}
