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

package com.charlesmuchene.prefedit.data

sealed interface Entry

data class BooleanEntry(val name: String, val value: Boolean) : Entry
data class StringEntry(val name: String, val value: String) : Entry
data class FloatEntry(val name: String, val value: Float) : Entry
data class LongEntry(val name: String, val value: Long) : Entry
data class IntEntry(val name: String, val value: Int) : Entry

data class SetEntry(val name: String, val entries: List<String>) : Entry
