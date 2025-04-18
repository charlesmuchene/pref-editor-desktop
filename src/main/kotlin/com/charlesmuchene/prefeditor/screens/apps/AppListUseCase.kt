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

package com.charlesmuchene.prefeditor.screens.apps

import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.processor.Processor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppListUseCase(
    private val processor: Processor,
    private val decoder: AppListDecoder,
    private val command: AppListCommand,
) {
    private val _status = MutableStateFlow<FetchStatus>(FetchStatus.Fetching)
    val status: StateFlow<FetchStatus> = _status.asStateFlow()

    suspend fun fetch() {
        _status.emit(FetchStatus.Fetching)
        val result = processor.run(command.command())
        val fetchStatus =
            if (result.isSuccess()) {
                FetchStatus.Done(decoder.decode(content = result.outputString))
            } else {
                FetchStatus.Error("Error fetching apps.")
            }
        _status.emit(fetchStatus)
    }

    sealed interface FetchStatus {
        data object Fetching : FetchStatus

        data class Done(val apps: Apps) : FetchStatus

        data class Error(val message: String) : FetchStatus
    }
}
