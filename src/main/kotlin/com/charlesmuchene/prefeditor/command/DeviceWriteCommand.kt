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
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.BACKUP
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.CHANGE
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.DELETE
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.REPLACE
import com.charlesmuchene.prefeditor.command.WriteCommand.Companion.SHELL
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.providers.TimestampProvider

/**
 * Device writing commands
 *
 * NOTE: Ensure the command order here matches `device.sh`'s expectation.
 */
class DeviceWriteCommand(
    private val app: App,
    private val device: Device,
    private val file: PrefFile,
    private val executable: String,
    private val timestamp: TimestampProvider,
) : WriteCommand {

    override fun MutableList<String>.delete(edit: Edit.Delete) {
        commandsWithMatcher(edit)
    }

    override fun MutableList<String>.change(edit: Edit.Change) {
        commandsWithMatcher(edit)
        add(edit.content.escaped())
    }

    override fun MutableList<String>.add(edit: Edit.Add) {
        commandsWithMatcher(edit)
        add(edit.content.escaped())
    }

    override fun MutableList<String>.replace(edit: Edit.Replace) {
        baseCommands()
        add(REPLACE)
        add(edit.content.escaped())
    }

    override fun MutableList<String>.backup(edit: Edit.Backup) {
        baseCommands()
        add(BACKUP)
        add("${file.filepath}.backup-${timestamp()}${file.suffix}")
    }

    private fun MutableList<String>.commandsWithMatcher(edit: Edit) {
        val (op, matcher) =
            when (edit) {
                is Edit.Add -> ADD to edit.matcher.escaped()
                is Edit.Change -> CHANGE to edit.matcher.escaped()
                is Edit.Delete -> DELETE to edit.matcher.escaped()
                else -> error("Unsupported operation")
            }
        baseCommands()
        add(op)
        add("i")
        add(matcher)
    }

    private fun MutableList<String>.baseCommands() {
        add(SHELL)
        add(SCRIPT)
        add(executable)
        add(device.serial)
        add(app.packageName)
        add(file.filepath)
    }

    companion object {
        private const val SCRIPT = "device.sh"
    }
}
