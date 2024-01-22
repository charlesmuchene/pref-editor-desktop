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

package com.charlesmuchene.prefedit.screens.device

import com.charlesmuchene.prefedit.app.AppState
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.command.ListApps
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.navigation.PrefList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppListingViewModel(
    private val appState: AppState,
    private val device: Device,
    private val bridge: Bridge,
    private val scope: CoroutineScope,
) :
    CoroutineScope by scope {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        launch { _uiState.emit(getApps()) }
    }

    private suspend fun getApps(): UIState {
        val result = bridge.execute(command = ListApps(device = device))
        return when {
            result.isSuccess -> result.getOrNull()?.let { apps ->
                if (apps.isEmpty()) UIState.Error
                else UIState.Apps(apps)
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    fun appSelected(app: App) {
        launch {
            appState.navigateTo(screen = PrefList(app = app, device = device))
        }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Apps(val apps: List<App>) : UIState
    }
}