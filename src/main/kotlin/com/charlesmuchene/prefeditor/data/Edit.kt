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

interface Edit {
    data class Delete(val name: String) : Edit
    data class Add(val name: String, val attributes: List<Attribute>, val value: String? = null) : Edit
    data class Change(val name: String, val attributes: List<Attribute> = emptyList(), val value: String? = null) : Edit

    data class Attribute(val name: String, val value: String)
}