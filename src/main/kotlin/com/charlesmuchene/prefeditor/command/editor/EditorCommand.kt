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

package com.charlesmuchene.prefeditor.command.editor

import com.charlesmuchene.prefeditor.data.Edit

interface EditorCommand {
    fun command(edit: Edit): List<String> = buildList {
        when (edit) {
            is Edit.Add -> add(edit = edit)
            is Edit.Change -> change(edit = edit)
            is Edit.Delete -> delete(edit = edit)
        }
    }

    fun delete(edit: Edit.Delete): List<String>
    fun change(edit: Edit.Change): List<String>
    fun add(edit: Edit.Add): List<String>

    fun String.escaped(): String = replace(oldValue = "/", newValue = "\\/")
        .replace(oldValue = "\"", newValue = "\\\"")
}