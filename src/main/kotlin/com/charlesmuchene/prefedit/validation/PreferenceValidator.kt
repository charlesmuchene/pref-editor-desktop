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

package com.charlesmuchene.prefedit.validation

import com.charlesmuchene.prefedit.data.*
import kotlin.reflect.KClass

class PreferenceValidator(private val original: List<Entry>) {

    fun validEdits(edits: Map<String, Pair<KClass<out Entry>, String>>): Boolean =
        original.fold(initial = false) { accumulator, entry ->
            accumulator || if (entry is SetEntry) false
            else (edits[entry.name]?.second != entry.value && edits[entry.name]?.let(::isValid) ?: false)
        }

    fun editsToPreferences(edits: Map<String, Pair<KClass<out Entry>, String>>): Preferences =
        Preferences(entries = buildList<Entry> {
            edits.entries.forEach { entry ->
                add(toEntry(klass = entry.value.first, name = entry.key, value = entry.value.second))
            }
            original.filterIsInstance<SetEntry>().forEach(::add)
        })

    private fun toEntry(klass: KClass<out Entry>, name: String, value: String): Entry = when (klass) {
        IntEntry::class -> IntEntry(name = name, value = value)
        LongEntry::class -> LongEntry(name = name, value = value)
        FloatEntry::class -> FloatEntry(name = name, value = value)
        BooleanEntry::class -> BooleanEntry(name = name, value = value)
        StringEntry::class -> StringEntry(name = name, value = value)
        else -> error("Unsupported entry: $klass")
    }

    fun isValid(entry: Entry, value: String): Boolean = isValid(entry::class to value)

    fun isValid(edits: Map<String, Pair<KClass<out Entry>, String>>): Boolean =
        edits.entries.fold(true) { accumulator, entry ->
            accumulator && isValid(pair = entry.value)
        }

    private fun isValid(pair: Pair<KClass<out Entry>, String>): Boolean = when (pair.first) {
        SetEntry::class, StringEntry::class -> true
        IntEntry::class -> pair.second.toIntOrNull() != null
        LongEntry::class -> pair.second.toLongOrNull() != null
        FloatEntry::class -> pair.second.toFloatOrNull() != null
        BooleanEntry::class -> pair.second.toBooleanStrictOrNull() != null
        else -> false
    }
}