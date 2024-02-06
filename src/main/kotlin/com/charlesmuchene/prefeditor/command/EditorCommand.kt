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
import com.charlesmuchene.prefeditor.data.Tags

interface EditorCommand {
    fun command(edit: Edit): List<String>

    fun String.escaped(): String = replace(oldValue = "/", newValue = "\\/")
        .replace(oldValue = "\"", newValue = "\\\"")
}

class DesktopEditorCommand(private val path: String) : EditorCommand {

    override fun command(edit: Edit): List<String> = buildList {
        when (edit) {
            is Edit.Add -> add(edit = edit)
            is Edit.Change -> change(edit = edit)
            is Edit.Delete -> delete(edit = edit)
        }
    }

    private fun delete(edit: Edit.Delete): List<String> = buildList {
        val matcher = edit.matcher.escaped()
        add("sh")
        add("delete.sh")
        add(matcher)
        add(path)
    }

    private fun change(edit: Edit.Change): List<String> = buildList {
        val matcher = edit.matcher.escaped()
        val content = edit.content.escaped()
        add("sh")
        add("change.sh")
        add(matcher)
        add(content)
        add(path)
    }

    private fun add(edit: Edit.Add): List<String> = buildList {
        val matcher = "</${Tags.ROOT}>".escaped()
        val content = edit.content.escaped()
        add("sh")
        add("add.sh")
        add(matcher)
        add(content)
        add(path)
    }

}