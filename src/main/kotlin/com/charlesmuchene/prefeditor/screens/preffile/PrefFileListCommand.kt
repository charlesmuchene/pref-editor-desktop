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

class PrefFileListCommand(
    private val app: App,
    private val device: Device,
    private val executable: String,
) : ReadCommand {
    override fun command(): List<String> =
        "$executable -s ${device.serial} shell run-as ${app.packageName} ls shared_prefs".split(ReadCommand.DELIMITER)
}
