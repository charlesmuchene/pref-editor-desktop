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

package com.charlesmuchene.prefeditor.validation

import com.charlesmuchene.prefeditor.data.*
import kotlin.reflect.KClass

/**
 * Validates edited preference entries
 *
 * @param original Preferences read from disk/device
 */
class PreferenceValidator(private val original: List<Preference>) {

    fun validEdits(edits: Map<String, Pair<KClass<out Preference>, String>>): Boolean =
        original.fold(initial = false) { accumulator, preference ->
            accumulator || if (preference is SetPreference) false
            else (edits[preference.name]?.second != preference.value && edits[preference.name]?.let(::isValid) ?: false)
        }

    fun editsToPreferences(edits: Map<String, Pair<KClass<out Preference>, String>>): Preferences =
        Preferences(preferences = buildList {
            val setPreferences = original.filterIsInstance<SetPreference>()
            val setPrefsNames = setPreferences.map(SetPreference::name).toSet()
            edits.entries.filter { it.key !in setPrefsNames }.forEach { pref ->
                add(toPreference(klass = pref.value.first, name = pref.key, value = pref.value.second))
            }
            setPreferences.forEach(::add)
        })

    private fun toPreference(klass: KClass<out Preference>, name: String, value: String): Preference = when (klass) {
        IntPreference::class -> IntPreference(name = name, value = value)
        LongPreference::class -> LongPreference(name = name, value = value)
        FloatPreference::class -> FloatPreference(name = name, value = value)
        BooleanPreference::class -> BooleanPreference(name = name, value = value)
        StringPreference::class -> StringPreference(name = name, value = value)
        else -> error("Unsupported preference: $klass")
    }

    /**
     * Determine the validity of the edited entries
     *
     * @param edits A developer's edits
     * @return `true` if all edits are valid, `false` otherwise
     */
    fun valid(edits: List<Preference>): Boolean = edits.all(::isValid)

    /**
     * Determine the validity of the edited preference
     *
     * @param preference Edited [Preference]
     * @return `true` if valid, `false` otherwise
     */
    fun isValid(preference: Preference): Boolean = when (preference) {
        is SetPreference, is StringPreference -> true
        is IntPreference -> preference.value.toIntOrNull() != null
        is LongPreference -> preference.value.toLongOrNull() != null
        is FloatPreference -> preference.value.toFloatOrNull() != null
        is BooleanPreference -> preference.value.toBooleanStrictOrNull() != null
    }

    /**
     * Determine the validity of edits
     *
     * @param edits A mapping of developer edits
     * @return `true` if all edits are valid, `false` otherwise
     */
    fun isValid(edits: Map<String, Pair<KClass<out Preference>, String>>): Boolean =
        edits.entries.fold(initial = true) { accumulator, preference ->
            accumulator && isValid(pair = preference.value)
        }

    private fun isValid(pair: Pair<KClass<out Preference>, String>): Boolean = when (pair.first) {
        SetPreference::class, StringPreference::class -> true
        IntPreference::class -> pair.second.toIntOrNull() != null
        LongPreference::class -> pair.second.toLongOrNull() != null
        FloatPreference::class -> pair.second.toFloatOrNull() != null
        BooleanPreference::class -> pair.second.toBooleanStrictOrNull() != null
        else -> false
    }
}