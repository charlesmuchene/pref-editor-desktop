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

import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.processor.ProcessorResult
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PrefFileListUseCase(
    private val commands: List<PrefFileListCommand>,
    private val processor: Processor,
    private val decoder: PrefFileListDecoder,
) {
    private val _status = MutableStateFlow<FetchStatus>(FetchStatus.Fetching)
    val status: StateFlow<FetchStatus> = _status.asStateFlow()

    suspend fun fetch() {
        _status.emit(FetchStatus.Fetching)
        val results = commands.map { processor.run(it.command()) }
        val output = results.filter(ProcessorResult::isSuccess).map(ProcessorResult::outputString)
        val fetchStatus = if (output.isEmpty()) {
            val message =
                if (results.any { !it.isSuccess }) "Error fetching preference files." else "No preference output"
            FetchStatus.Error(message)
        } else {
            FetchStatus.Done(decoder.decode(contents = output))
        }
        _status.emit(fetchStatus)
    }

    sealed interface FetchStatus {
        data object Fetching : FetchStatus

        data class Error(val message: String) : FetchStatus

        data class Done(val result: PrefFileResult) : FetchStatus
    }
}
