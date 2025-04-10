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

import com.charlesmuchene.prefeditor.data.PrefFile.Type.DATA_STORE
import com.charlesmuchene.prefeditor.data.PrefFile.Type.KEY_VALUE

typealias PrefFiles = List<PrefFile>

data class PrefFile(val name: String, val type: Type) {

    val filepath: String get() = "${type.path}/${name}"

    val suffix: String
        get() = when (type) {
            KEY_VALUE -> ".xml"
            DATA_STORE -> ".pb"
        }

    enum class Type(val text: String) {
        KEY_VALUE(text = "Key-Value"),
        DATA_STORE(text = "Datastore");

        val path: String
            get() = when (this) {
                KEY_VALUE -> "shared_prefs"
                DATA_STORE -> "files/datastore"
            }
    }
}
