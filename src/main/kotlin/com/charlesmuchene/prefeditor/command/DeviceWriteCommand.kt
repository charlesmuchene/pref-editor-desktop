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
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.SHELL
import com.charlesmuchene.prefeditor.data.*

class DeviceWriteCommand(
    private val app: App,
    private val device: Device,
    private val file: PrefFile,
) : WriteCommand {

    override fun MutableList<String>.delete(edit: Edit.Delete) {
        baseCommands(DELETE)
        add(edit.matcher.escaped())
        add(file.name)
    }

    override fun MutableList<String>.change(edit: Edit.Change) {
        baseCommands(CHANGE)
        val matcher = edit.matcher.escaped()
        val content = edit.content.escaped()
        add(matcher)
        add(content)
        add(file.name)
    }

    override fun MutableList<String>.add(edit: Edit.Add) {
        baseCommands(ADD)
        val matcher = "</${Tags.ROOT}>".escaped()
        val content = edit.content.escaped()
        add(matcher)
        add(content)
        add(file.name)
    }

    private fun MutableList<String>.baseCommands(edit: String) {
        add(SHELL)
        add(SCRIPT)
        add(edit)
        add(device.serial)
        add(app.packageName)
        add("i")
    }

    companion object {
        private const val SCRIPT = "device.sh"
    }
}