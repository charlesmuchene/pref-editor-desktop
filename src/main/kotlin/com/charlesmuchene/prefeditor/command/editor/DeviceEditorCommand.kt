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

import com.charlesmuchene.prefeditor.data.*

class DeviceEditorCommand(
    private val app: App,
    private val device: Device,
    private val file: PrefFile,
) : EditorCommand {

    override fun delete(edit: Edit.Delete): List<String> = buildList {
        baseCommands()
        add(edit.matcher.escaped())
    }

    override fun change(edit: Edit.Change): List<String> = buildList {
        baseCommands()
        val matcher = edit.matcher.escaped()
        val content = edit.content.escaped()
        add(matcher)
        add(content)
    }

    override fun add(edit: Edit.Add): List<String> = buildList {
        baseCommands()
        val matcher = "</${Tags.ROOT}>".escaped()
        val content = edit.content.escaped()
        add(matcher)
        add(content)
    }

    private fun MutableList<String>.baseCommands() {
        add(SHELL)
        add(SCRIPT)
        add(device.serial)
        add(app.packageName)
        add("i")
        add(file.name)
    }

    companion object {
        private const val SHELL = "sh"
        private const val SCRIPT = "push.sh"
    }
}