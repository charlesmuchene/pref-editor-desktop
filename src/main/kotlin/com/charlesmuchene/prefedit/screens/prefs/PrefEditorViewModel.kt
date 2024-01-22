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

package com.charlesmuchene.prefedit.screens.prefs

import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.command.FetchPref
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.PrefFile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrefEditorViewModel(
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val prefFile: PrefFile,
    private val scope: CoroutineScope,
) : CoroutineScope by scope {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        launch { _uiState.emit(fetchPreferences()) }
    }

    private suspend fun fetchPreferences(): UIState {
        val result = bridge.execute(command = FetchPref(app = app, device = device, prefFile = prefFile))
        return when {
            result.isSuccess -> result.getOrNull()?.let { pref ->
                UIState.Preferences(preferences = pref)
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Preferences(val preferences: com.charlesmuchene.prefedit.data.Preferences) : UIState
    }
}