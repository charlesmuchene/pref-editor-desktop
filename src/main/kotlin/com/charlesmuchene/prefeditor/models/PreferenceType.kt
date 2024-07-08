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

package com.charlesmuchene.prefeditor.models

import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.datastore.preferences.StringSetPreference

enum class PreferenceType(val icon: kotlin.String) {
    Boolean(icon = "spherical"),
    Float(icon = "cylindrical"),
    Integer(icon = "conical"),
    Long(icon = "pyramidical"),
    Set(icon = "triangular"),
    String(icon = "cubical"),
}

fun preferenceIconName(preference: Preference): String =
    when (preference) {
        is BooleanPreference -> PreferenceType.Boolean.icon
        is StringPreference -> PreferenceType.String.icon
        is FloatPreference -> PreferenceType.Float.icon
        is IntPreference -> PreferenceType.Integer.icon
        is LongPreference -> PreferenceType.Long.icon
        is StringSetPreference -> PreferenceType.Set.icon
        else -> PreferenceType.Integer.icon
    }
