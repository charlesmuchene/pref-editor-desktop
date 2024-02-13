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

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.extensions.throttleLatest
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PreferencesViewModel(
    app: App,
    device: Device,
    readOnly: Boolean,
    prefFile: PrefFile,
    reloadSignal: ReloadSignal,
    private val scope: CoroutineScope,
) : CoroutineScope by scope {

    private val processor = Processor()
    private val codec = PreferencesCodec()
    val useCase =
        DevicePreferencesUseCase(prefCodec = codec, app = app, device = device, file = prefFile, processor = processor)

    private var isReadyOnly = readOnly
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        useCase.preferences
            .onEach { _uiState.emit(UIState.Success(preferences = it, readOnly = isReadyOnly)) }
            .launchIn(scope = scope)

        reloadSignal.signal
            .onEach { _uiState.emit(UIState.Loading) }
            .throttleLatest(delayMillis = 300)
            .onEach { useCase.readPreferences() }
            .launchIn(scope = scope)
    }

    fun edit() {
        val success = _uiState.value
        if (success is UIState.Success) {
            isReadyOnly = !isReadyOnly
            launch { _uiState.emit(success.copy(readOnly = isReadyOnly)) }
        }
    }

    sealed interface UIState {
        data object Loading : UIState
        data class Error(val message: String? = null) : UIState
        data class Success(val preferences: Preferences, val readOnly: Boolean) : UIState
    }
}