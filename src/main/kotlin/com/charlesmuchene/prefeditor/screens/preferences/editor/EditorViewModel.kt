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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import org.jetbrains.jewel.ui.Outline
import kotlin.coroutines.CoroutineContext

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
    private val validator: PreferenceValidator = PreferenceValidator(original = preferences.entries),
) : CoroutineScope by scope + context {

    private var enableBackup = false
    private val _message = MutableSharedFlow<String?>()
    private val changes = MutableSharedFlow<CharSequence>()
    private val edits = preferences.entries.associate(Entry::toPair).toMutableMap()

    private val original = preferences.entries.associate { it.name to it.value }

    // TODO Rename to edits
    private val edited = preferences.entries.map(::UIEntry).associateBy { it.entry.name }.toMutableMap()
    private val _entries = mutableStateOf(edited.values.toList())
    val entries: State<List<UIEntry>> = _entries
    val message: SharedFlow<String?> = _message.asSharedFlow()

    val enableSave: StateFlow<Boolean> = changes
        .map { validator.validEdits(edits) }
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = true)

    /**
     * Create entries to display a string set
     *
     * @param entry [SetEntry]
     * @return [List] of [SetSubEntry.Entry]
     */
    fun createSubEntries(entry: SetEntry): Pair<SetSubEntry.Header, List<SetSubEntry.Entry>> =
        Pair(SetSubEntry.Header(entry.name), entry.entries.map(SetSubEntry::Entry))

    /**
     * Save edits
     */
    fun save() {
        launch {
            val entries = edited.values.filter { it.state == EntryState.Changed }.map(UIEntry::entry)
            val isValid = validator.valid(entries)
            if (isValid) saveChanges() else showInvalidEdits()
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
     * Determine entry outline
     *
     * @param entry Edited [Entry]
     * @return UI [Outline] to apply
     */
    fun outline(entry: Entry): Outline = when (entry) {
        is FloatEntry, is IntEntry, is LongEntry -> numberOutline(entry = entry)
        is BooleanEntry, is StringEntry -> if (original[entry.name] == entry.value) Outline.None else Outline.Warning
        else -> Outline.None
    }

    /**
     * Determine outline for a number entry
     *
     * @param entry Number [Entry]: Int, Long, Float
     * @return [Outline] instance
     */
    private fun numberOutline(entry: Entry): Outline {
        if (original[entry.name] == entry.value) return Outline.None
        return if (validator.isValid(entry = entry)) Outline.Warning else Outline.Error
    }

    /**
     * Entry action
     *
     * @param action Developer [EntryAction]
     * @return Resulting [UIEntry] instance
     */
    fun entryAction(action: EntryAction): UIEntry = when (action) {
        is EntryAction.Change -> entryChanged(action.entry, action.change)
        is EntryAction.Delete -> entryDeleted(action.entry)
        is EntryAction.Reset -> entryReset(action.entry)
    }

    /**
     * Entry reset
     *
     * @param entry Reset [Entry]
     * @return Unedited [UIEntry]
     */
    private fun entryReset(entry: Entry): UIEntry {
        val value = original[entry.name] ?: error("Missing entry value ${entry.name}")
        return UIEntry(createEntry(oldEntry = entry, value = value)).also { edited[entry.name] = it }
    }

    /**
     * Entry deleted
     *
     * @param entry Deleted [Entry]
     * @return [UIEntry] with [EntryState.Deleted] state
     */
    private fun entryDeleted(entry: Entry): UIEntry {
        return UIEntry(entry = entry, state = EntryState.Deleted).also { edited[entry.name] = it }
    }

    /**
     * Entry content changed
     *
     * @param entry Displayed [Entry]
     * @param change [Entry] content change
     * @return [UIEntry] with the [EntryState.Changed] if [entry] differs from original,
     * [EntryState.None] otherwise
     */
    private fun entryChanged(entry: Entry, change: String): UIEntry {
//        launch { changes.emit(change) }

        val newEntry = createEntry(oldEntry = entry, value = change)
        val state = if (original[entry.name] == newEntry.value) EntryState.None else EntryState.Changed
        return UIEntry(entry = newEntry, state = state).also { edited[entry.name] = it }
    }

    /**
     * Create an entry during edits
     *
     * @param oldEntry Existing [Entry] to evaluate type
     * @param value Entry value
     * @return The created [Entry]
     */
    private fun createEntry(oldEntry: Entry, value: String) = when (oldEntry) {
        is BooleanEntry -> BooleanEntry(oldEntry.name, value = value)
        is FloatEntry -> FloatEntry(oldEntry.name, value = value)
        is IntEntry -> IntEntry(oldEntry.name, value = value)
        is LongEntry -> LongEntry(oldEntry.name, value = value)
        is StringEntry -> StringEntry(oldEntry.name, value = value)
        else -> error("Changing $oldEntry is not supported")
    }
}