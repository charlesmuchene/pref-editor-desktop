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

package com.charlesmuchene.prefeditor.command

import com.charlesmuchene.prefeditor.data.Edit

interface WriteCommand {
    fun command(edit: Edit): List<String> =
        buildList {
            when (edit) {
                is Edit.Add -> add(edit = edit)
                is Edit.Change -> change(edit = edit)
                is Edit.Delete -> delete(edit = edit)
                is Edit.Replace -> replace(edit = edit)
            }
        }

    fun MutableList<String>.delete(edit: Edit.Delete)

    fun MutableList<String>.change(edit: Edit.Change)

    fun MutableList<String>.add(edit: Edit.Add)

    fun MutableList<String>.replace(edit: Edit.Replace) {
        error("Unsupported command")
    }

    fun String.escaped(): String =
        replace(oldValue = "/", newValue = "\\/")
            .replace(oldValue = "\"", newValue = "\\\"")

    companion object {
        const val SHELL = "sh"
        const val ADD = "add"
        const val CHANGE = "change"
        const val DELETE = "delete"
        const val REPLACE = "replace"
    }
}
