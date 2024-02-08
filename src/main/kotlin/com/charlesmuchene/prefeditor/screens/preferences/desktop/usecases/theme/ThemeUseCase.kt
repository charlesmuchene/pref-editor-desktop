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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.extensions.throttleLatest
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme.System
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

class ThemeUseCase(
    private val codec: ThemeCodec,
    private val editor: PreferenceWriter,
    private val path: Path = EditorFiles.preferencesPath(),
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by CoroutineScope(context) {

    private val _theme = mutableStateOf(System)
    var theme: State<EditorTheme> = _theme

    private val saveFlow = MutableSharedFlow<EditorTheme>()

    init {
        launch { saveFlow.throttleLatest(delayMillis = 2_000).collect(::saveTheme) }
    }

    fun changeTheme(theme: EditorTheme) {
        _theme.value = theme
        launch { saveFlow.emit(theme) }
    }

    suspend fun loadTheme() {
        val decoded = codec.decode(path = path)
        _theme.value = decoded ?: System
        logger.debug { "Load Theme: $decoded" }
    }

    private suspend fun saveTheme(theme: EditorTheme) {
        val edit = codec.encode(theme = theme)
        val result = editor.edit(edit = edit)
        if (result.isFailure) logger.error(result.exceptionOrNull()) {}
    }
}