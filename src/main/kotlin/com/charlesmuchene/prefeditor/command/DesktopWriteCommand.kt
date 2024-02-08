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

import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.ADD
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.CHANGE
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.DELETE
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.Tags

class DesktopWriteCommand(private val path: String) : WriteCommand {

    override fun MutableList<String>.delete(edit: Edit.Delete) {
        val matcher = edit.matcher.escaped()
        add(SHELL)
        add(SCRIPT)
        add(DELETE)
        add(matcher)
        add(path)
    }

    override fun MutableList<String>.change(edit: Edit.Change) {
        val matcher = edit.matcher.escaped()
        val content = edit.content.escaped()
        add(SHELL)
        add(SCRIPT)
        add(CHANGE)
        add(matcher)
        add(content)
        add(path)
    }

    override fun MutableList<String>.add(edit: Edit.Add) {
        val matcher = "</${Tags.ROOT}>".escaped()
        val content = edit.content.escaped()
        add(SHELL)
        add(SCRIPT)
        add(ADD)
        add(matcher)
        add(content)
        add(path)
    }

    companion object {
        private const val SHELL = "sh"
        private const val SCRIPT = "desktop.sh"
    }

}