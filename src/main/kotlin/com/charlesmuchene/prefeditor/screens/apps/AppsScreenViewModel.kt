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

import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.command.ListApps
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefListScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppsScreenViewModel(
    private val device: Device,
    private val bridge: Bridge,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
) : CoroutineScope by scope {

    private val apps = mutableListOf<App>()
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
                else UIState.Apps(apps).also {
                    this.apps.addAll(it.apps)
                }
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    fun appSelected(app: App) {
        launch {
            navigation.navigate(screen = PrefListScreen(app = app, device = device))
        }
    }

    fun filter(input: String) {
        launch { _uiState.emit(UIState.Apps(apps.filter { it.packageName.contains(input, ignoreCase = true) })) }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Apps(val apps: List<App>) : UIState
    }
}