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

package com.charlesmuchene.prefeditor.screens.preferences.editor

import com.charlesmuchene.prefeditor.command.reader.ReadPreferencesCommand
import com.charlesmuchene.prefeditor.command.writer.DeviceEditorCommand
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.preferences.PreferenceReader
import com.charlesmuchene.prefeditor.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.processor.Processor

class DevicePreferencesUseCase(
    app: App,
    file: PrefFile,
    device: Device,
    processor: Processor,
    prefCodec: PreferencesCodec,
) {
    private val codec = DevicePreferencesCodec(codec = prefCodec)
    private val command = DeviceEditorCommand(app = app, device = device, file = file)
    private val writer = PreferenceWriter(processor = processor, command = command)
    private val reader = PreferenceReader(processor = processor)

    private lateinit var preferences: Preferences

    suspend fun readPreferences(file: PrefFile, app: App, device: Device): Preferences {
        val command = ReadPreferencesCommand(app = app, device = device, prefFile = file)
        val content = reader.read(command)
        return codec.decode(content = content).also { preferences = it }
    }

    suspend fun writePreferences(preferences: Collection<UIPreference>): String {
        val edits = preferences.filter { it.state !is PreferenceState.None }
        val content = codec.encode(edits = edits, existing = this.preferences.preferences)
        return writer.edit(content)
    }
}