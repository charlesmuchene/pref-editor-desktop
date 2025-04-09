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

package com.charlesmuchene.prefeditor.screens.preffile

import com.charlesmuchene.prefeditor.command.ReadCommand
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device

sealed interface PrefFileListCommand : ReadCommand {
    companion object {
        fun create(app: App, device: Device, executable: String) = listOf(
            KeyValuePrefFileListCommand(app = app, device = device, executable = executable),
            DatastorePrefFileListCommand(app = app, device = device, executable = executable)
        )
    }
}

data class KeyValuePrefFileListCommand(
    private val app: App,
    private val device: Device,
    private val executable: String,
) : PrefFileListCommand {
    override fun command(): List<String> =
        "$executable -s ${device.serial} shell run-as ${app.packageName} ls shared_prefs".split(ReadCommand.DELIMITER)
}

data class DatastorePrefFileListCommand(
    private val app: App,
    private val device: Device,
    private val executable: String,
) : PrefFileListCommand {
    override fun command(): List<String> =
        "$executable -s ${device.serial} shell run-as ${app.packageName} [ -d files/datastore ] && ls files/datastore"
            .split(ReadCommand.DELIMITER)
}
