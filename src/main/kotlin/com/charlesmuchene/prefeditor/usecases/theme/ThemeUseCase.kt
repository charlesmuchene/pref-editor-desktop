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

import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor
import com.charlesmuchene.prefeditor.usecases.theme.EditorTheme.System
import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

class ThemeUseCase(
    private val codec: ThemeCodec = ThemeCodec(PreferencesCodec()),
    private val editor: PreferenceEditor = PreferenceEditor(),
) {

    suspend fun loadTheme(): EditorTheme {
        val path = EditorFiles.preferencesPath()
        return codec.decode(path = path) ?: System
    }

    suspend fun saveTheme(theme: EditorTheme) {
        val path = EditorFiles.preferencesPath()
        val edit = codec.encode(theme = theme)
        val output = editor.edit(edit, path)
        logger.debug { "Save Theme: $output" }
    }
}