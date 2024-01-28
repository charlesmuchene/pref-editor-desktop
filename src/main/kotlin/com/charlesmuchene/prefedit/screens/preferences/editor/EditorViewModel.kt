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

package com.charlesmuchene.prefedit.screens.preferences.editor

import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.command.WritePref
import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.SetSubEntry
import com.charlesmuchene.prefedit.validation.PreferenceValidator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.Outline

class EditorViewModel(
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val scope: CoroutineScope,
    private val preferences: Preferences,
    private val validator: PreferenceValidator = PreferenceValidator(original = preferences.entries),
) : CoroutineScope by scope {

    private val edits = preferences.entries.associate(Entry::toPair).toMutableMap()
    val prefs = preferences.entries.partition { it is SetEntry }

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

    private fun invalidEdits() {
        // TODO Indicate invalid prefs
        println("Invalid preferences")
    }

    private suspend fun pushPrefs() {
        val preferences = validator.editsToPreferences(edits = edits)
        val command = WritePref(app = app, device = device, prefFile = prefFile, preferences = preferences)
        val result = bridge.execute(command).getOrNull() ?: false
        if (!result) println("Error writing preferences")
    }
}