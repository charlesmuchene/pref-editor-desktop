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
import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.SetSubEntry
import kotlinx.coroutines.CoroutineScope
import org.jetbrains.jewel.ui.Outline

class EditorViewModel(
    preferences: Preferences,
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val scope: CoroutineScope,
) : CoroutineScope by scope {

    val prefs = preferences.entries.partition { it is SetEntry }

    fun setUIEntries(entry: SetEntry): Pair<SetSubEntry.Header, List<SetSubEntry.Entry>> =
        Pair(SetSubEntry.Header(entry.name), entry.entries.map(SetSubEntry::Entry))

    fun outline(entry: BooleanEntry, value: Boolean): Pair<Outline, Outline> {
        val trueUIOutline = if (!entry.value && value) Outline.Warning else Outline.None
        val falseUIOutline = if (entry.value && !value) Outline.Warning else Outline.None
        return Pair(trueUIOutline, falseUIOutline)
    }

    fun outline(entry: Entry, value: String): Outline = when (entry) {
        is FloatEntry -> numberOutline(
            converter = String::toFloatOrNull,
            initialValue = entry.value,
            newValue = value,
        )

        is IntEntry -> numberOutline(
            converter = String::toIntOrNull,
            initialValue = entry.value,
            newValue = value,
        )

        is LongEntry -> numberOutline(
            converter = String::toLongOrNull,
            initialValue = entry.value,
            newValue = value,
        )

        else -> Outline.None
    }

    private fun <T> numberOutline(initialValue: T, newValue: String, converter: (String) -> T?): Outline {
        val result = converter(newValue) ?: return Outline.Error
        return if (initialValue == result) Outline.None else Outline.Warning
    }

}