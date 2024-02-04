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

package com.charlesmuchene.prefeditor.usecases.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor
import com.charlesmuchene.prefeditor.usecases.theme.EditorTheme.*
import io.github.oshai.kotlinlogging.KotlinLogging
import java.nio.file.Path

private val logger = KotlinLogging.logger {}

class ThemeUseCase(
    private val codec: ThemeCodec,
    private val editor: PreferenceEditor,
    private val path: Path = EditorFiles.preferencesPath(),
) {

    private val _theme = mutableStateOf(System)
    var theme: State<EditorTheme> = _theme

    fun switchTheme() {
        _theme.value = when (_theme.value) {
            Light -> Dark
            Dark -> System
            System -> Light
        }
    }

    suspend fun loadTheme() {
        val decoded = codec.decode(path = path)
        _theme.value = decoded ?: System
        logger.debug { "Load Theme: $decoded" }
    }

    suspend fun saveTheme() {
        val edit = codec.encode(theme = _theme.value)
        val output = editor.edit(edit = edit, path = path)
        logger.debug { "Save Theme: $theme -> $output" }
    }
}