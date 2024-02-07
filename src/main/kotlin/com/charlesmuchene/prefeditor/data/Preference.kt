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

package com.charlesmuchene.prefeditor.data

sealed class Preference(open val name: String, open val value: String) {
    fun toPair() = name to value
}

data class BooleanPreference(override val name: String, override val value: String) : Preference(name = name, value = value)
data class StringPreference(override val name: String, override val value: String) : Preference(name = name, value = value)
data class FloatPreference(override val name: String, override val value: String) : Preference(name = name, value = value)
data class LongPreference(override val name: String, override val value: String) : Preference(name = name, value = value)
data class IntPreference(override val name: String, override val value: String) : Preference(name = name, value = value)

data class SetPreference(override val name: String, val entries: List<String>) :
    Preference(name = name, value = entries.joinToString())
