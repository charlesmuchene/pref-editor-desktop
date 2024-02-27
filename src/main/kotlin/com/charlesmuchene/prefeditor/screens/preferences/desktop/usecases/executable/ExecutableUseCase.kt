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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.executable

import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.nio.file.Path

private val logger = KotlinLogging.logger { }

class ExecutableUseCase(
    private val prefPath: Path,
    private val codec: ExecutableCodec,
    private val writer: PreferenceWriter,
) {
    private val _path = MutableStateFlow<Path?>(null)
    val path: StateFlow<Path?> = _path.asStateFlow()

    suspend fun readExecutable() {
        val decoded = codec.decode(prefPath)
        _path.emit(decoded)
    }

    suspend fun saveExecutable(path: Path) {
        val edit = codec.encode(path = path)
        writer.edit(edit)
    }
}
