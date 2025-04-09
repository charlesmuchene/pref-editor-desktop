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
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceReader
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.screens.preferences.device.codec.DevicePreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

private val logger = KotlinLogging.logger { }

class DevicePreferencesUseCase(
    val file: PrefFile,
    private val writer: PreferenceWriter,
    private val reader: PreferenceReader,
    private val codec: DevicePreferencesCodec,
) {
    val backup = mutableStateOf(false)

    private val _preferences = MutableStateFlow<Preferences>(KeyValuePreferences(preferences = emptyList()))
    val preferences: StateFlow<Preferences> = _preferences.asStateFlow()

    suspend fun readPreferences() {
        _preferences.emit(KeyValuePreferences(emptyList()))
        val (type, result) = reader.read()
        if (result.isSuccess()) {
            _preferences.emit(codec.decode(content = result.output, type = type))
        } else {
            logger.error { "Failure when reading preferences" }
        }
    }

    suspend fun writePreferences(preferences: Collection<UIPreference>): Boolean {
        val edits = preferences.filter { it.state !is PreferenceState.None }
        val existing = this.preferences.value as? KeyValuePreferences ?: return false
        val content = codec.encode(edits = edits, existing = existing.preferences)
        return writer.edit(content).all { it.isSuccess() }.also { readPreferences() }
    }

    suspend fun addPreference(preference: Preference): Boolean {
        val list = listOf(UIPreference(preference, state = PreferenceState.New))
        return writePreferences(list)
    }
}
