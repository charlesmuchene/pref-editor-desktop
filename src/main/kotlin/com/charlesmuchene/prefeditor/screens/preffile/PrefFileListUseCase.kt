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

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.EmptyPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefFileListUseCase(
    app: App,
    device: Device,
    private val processor: Processor,
    private val decoder: PrefFileListDecoder,
) {
    private val command = PrefFileListCommand(app = app, device = device)
    private val _prefFileResult = MutableStateFlow<PrefFileResult>(EmptyPrefs)
    val fileResult: StateFlow<PrefFileResult> = _prefFileResult.asStateFlow()

    suspend fun list(): Result<PrefFileResult> = processor.run(command.command()).map { content ->
        decoder.decode(content = content).also { _prefFileResult.emit(it) }
    }
}