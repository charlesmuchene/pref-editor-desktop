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
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefListScreen
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.SetSubEntry
import com.charlesmuchene.prefeditor.validation.PreferenceValidator
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.Outline

private val logger = KotlinLogging.logger {}

sealed interface EntryState {
    data object Changed : EntryState
    data object Deleted : EntryState
    data object None : EntryState
}

data class UIEntry(val entry: Entry, val state: EntryState = EntryState.None)

sealed interface EntryAction {
    data class Reset(val entry: Entry) : EntryAction
    data class Delete(val entry: Entry) : EntryAction
    data class Change(val entry: Entry, val change: String) : EntryAction
}

class EditorViewModel(
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val appState: AppState,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val preferences: Preferences,
    private val validator: PreferenceValidator = PreferenceValidator(original = preferences.entries),
) : CoroutineScope by scope {

    private var enableBackup = false
    private val _message = MutableSharedFlow<String?>()
    private val changes = MutableSharedFlow<CharSequence>()
    private val edits = preferences.entries.associate(Entry::toPair).toMutableMap()

    private val original = preferences.entries.associate { it.name to it.value }
    private val edited = preferences.entries.map(::UIEntry).associateBy { it.entry.name }.toMutableMap()
    private val _entries = mutableStateOf(edited.values.toList())
    val entries: State<List<UIEntry>> = _entries
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> = changes
        .map { validator.validEdits(edits) }
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = false)

    fun createSetSubEntries(entry: SetEntry): Pair<SetSubEntry.Header, List<SetSubEntry.Entry>> =
        Pair(SetSubEntry.Header(entry.name), entry.entries.map(SetSubEntry::Entry))

    fun outline(entry: Entry, value: String): Outline = when (entry) {
        is FloatEntry, is IntEntry, is LongEntry -> numberOutline(entry = entry, value = value)

        is BooleanEntry, is StringEntry -> if (entry.value == value) Outline.None else Outline.Warning

        else -> Outline.None
    }

    private fun numberOutline(entry: Entry, value: String): Outline {
        if (entry.value == value) return Outline.None
        return if (validator.isValid(entry, value)) Outline.Warning else Outline.Error
    }

    fun save() {
        launch {
            val isValid = validator.isValid(edits = edits)
            if (isValid) pushPrefs() else invalidEdits()
        }
    }

    fun backup(backup: Boolean) {
        enableBackup = backup
    }

    private fun invalidEdits() {
        val message = "Invalid edits"
        launch { _message.emit(message) }
        logger.info { "$message: $edits" }
    }

    private suspend fun pushPrefs() {
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

    fun entryAction(action: EntryAction) {
        when (action) {
            is EntryAction.Change -> entryChanged(action.entry, action.change)
            is EntryAction.Delete -> entryDeleted(action.entry)
            is EntryAction.Reset -> entryReset(action.entry)
        }
    }

    private fun entryReset(entry: Entry) {

    }

    private fun entryDeleted(entry: Entry) {

    }

    private fun entryChanged(entry: Entry, change: String) {
        val value = edited[entry.name] ?: return
        launch { changes.emit(change) }

        val newEntry = when (val oldEntry = value.entry) {
            is BooleanEntry -> oldEntry.copy(value = change)
            is FloatEntry -> oldEntry.copy(value = change)
            is IntEntry -> oldEntry.copy(value = change)
            is LongEntry -> oldEntry.copy(value = change)
            is StringEntry -> oldEntry.copy(value = change)
            else -> error("Changing $oldEntry is not supported")
        }

        val state = if (original[entry.name] == newEntry.value) EntryState.None else EntryState.Changed
        edited[entry.name] = UIEntry(entry = newEntry, state = state)
        _entries.value = edited.values.toList()
    }
}