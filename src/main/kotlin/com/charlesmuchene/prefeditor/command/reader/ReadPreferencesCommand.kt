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

package com.charlesmuchene.prefeditor.command.reader

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile

class ReadPreferencesCommand(
    private val app: App,
    private val device: Device,
    private val prefFile: PrefFile,
) : ReaderCommand {
    override fun command(): List<String> {
        return "adb -s ${device.serial} exec-out run-as ${app.packageName} cat /data/data/${app.packageName}/shared_prefs/${prefFile.name}"
            .split(ReaderCommand.DELIMITER)
    }
}