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

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.processor.Processor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppListUseCase(device: Device, private val processor: Processor, private val decoder: AppListDecoder) {

    private val command = AppListCommand(device = device)
    private val _apps = MutableStateFlow(emptyList<App>())
    val apps: StateFlow<Apps> = _apps.asStateFlow()

    suspend fun list(): Result<Apps> = processor.run(command.command()).map { content ->
        decoder.decode(content = content).also { _apps.emit(it) }
    }

}