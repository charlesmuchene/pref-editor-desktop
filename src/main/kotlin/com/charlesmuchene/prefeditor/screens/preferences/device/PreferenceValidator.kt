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

package com.charlesmuchene.prefeditor.screens.preferences.device

import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.datastore.preferences.ByteArrayPreference
import com.charlesmuchene.datastore.preferences.DoublePreference
import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.datastore.preferences.StringSetPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference

/**
 * Validates edited preference entries
 */
class PreferenceValidator {
    /**
     * Determine there are any edited preferences
     *
     * @param edits Edits handle
     * @return `true` if any prefs are edited, `false` otherwise
     */
    fun hasEdits(edits: Map<String, UIPreference>): Boolean {
        return edits.values.any { it.state is PreferenceState.Deleted || it.state is PreferenceState.Changed }
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
    fun isValid(preference: Preference): Boolean =
        when (preference) {
            is StringSetPreference, is StringPreference, is ByteArrayPreference -> true
            is IntPreference -> preference.value.toIntOrNull() != null
            is LongPreference -> preference.value.toLongOrNull() != null
            is FloatPreference -> preference.value.toFloatOrNull() != null
            is BooleanPreference -> preference.value.toBooleanStrictOrNull() != null
            is DoublePreference -> preference.value.toDoubleOrNull() != null
        }
}
