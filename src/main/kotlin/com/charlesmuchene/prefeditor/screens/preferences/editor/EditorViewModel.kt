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

class EditorViewModel(
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val preferences: Preferences,
    private val validator: PreferenceValidator = PreferenceValidator(original = preferences.entries),
) : CoroutineScope by scope {

    private var enableBackup = false
    private val _message = MutableSharedFlow<String?>()
    private val changes = MutableSharedFlow<CharSequence>()
    private val edits = preferences.entries.associate(Entry::toPair).toMutableMap()
    val prefs = preferences.entries.partition { it is SetEntry }
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

    fun edited(entry: Entry, change: String) {
        launch { changes.emit(change) }
        if (entry !is SetEntry) {
            val value = edits[entry.name] ?: return
            edits[entry.name] = value.copy(second = change)
        }
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
                _message.emit("Saving successful")
                navigation.navigate(screen = PrefListScreen(app = app, device = device))
            }

            else -> {
                val message = "Error saving preferences"
                _message.emit(message)
                logger.error(result.exceptionOrNull()) { message }
            }
        }
    }
}