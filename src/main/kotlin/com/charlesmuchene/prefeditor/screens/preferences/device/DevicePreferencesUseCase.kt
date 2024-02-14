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

package com.charlesmuchene.prefeditor.screens.preferences.device

import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.command.DeviceWriteCommand
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.providers.TimeStampProviderImpl
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceReader
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val logger = KotlinLogging.logger { }

class DevicePreferencesUseCase(
    val file: PrefFile,
    private val app: App,
    private val device: Device,
    processor: Processor,
    prefCodec: PreferencesCodec,
) {
    val backup = mutableStateOf(false)

    private val codec = DevicePreferencesCodec(codec = prefCodec)
    private val timestamp = TimeStampProviderImpl()
    private val command =
        DeviceWriteCommand(app = app, device = device, file = file, backup = backup, timestamp = timestamp)
    private val writer = PreferenceWriter(processor = processor, command = command)
    private val reader = PreferenceReader(processor = processor)

    private val _preferences = MutableStateFlow(Preferences(preferences = emptyList()))
    val preferences: StateFlow<Preferences> = _preferences.asStateFlow()

    suspend fun readPreferences() {
        _preferences.emit(Preferences(emptyList()))
        val command = PreferencesCommand(app = app, device = device, prefFile = file)
        reader.read(command)
            .onSuccess { content -> _preferences.emit(codec.decode(content = content)) }
            .onFailure { logger.error(it) { "Failure when reading preferences" } }
    }

    suspend fun writePreferences(preferences: Collection<UIPreference>): List<Result<String>> {
        val edits = preferences.filter { it.state !is PreferenceState.None }
        val content = codec.encode(edits = edits, existing = this.preferences.value.preferences)
        return writer.edit(content).also { readPreferences() }
    }

    suspend fun addPreference(preference: Preference): Result<String> {
        val list = listOf(UIPreference(preference, state = PreferenceState.New))
        return writePreferences(list).first()
    }
}