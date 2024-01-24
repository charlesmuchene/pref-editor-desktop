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

package com.charlesmuchene.prefedit.screens.listing

import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.command.ListPrefFiles
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.PrefFile
import com.charlesmuchene.prefedit.data.PrefFiles
import com.charlesmuchene.prefedit.navigation.Navigation
import com.charlesmuchene.prefedit.navigation.PrefEditScreen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PrefListingViewModel(
    private val app: App,
    private val device: Device,
    private val bridge: Bridge,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
) : CoroutineScope by scope {

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        launch {
            _uiState.emit(getPrefFiles())
        }
    }

    private suspend fun getPrefFiles(): UIState {
        val result = bridge.execute(command = ListPrefFiles(app = app, device = device))
        return when {
            result.isSuccess -> result.getOrNull()?.let { prefFiles ->
                UIState.Files(prefFiles)
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    fun fileSelected(prefFile: PrefFile) {
        launch {
            navigation.navigate(screen = PrefEditScreen(prefFile = prefFile, app = app, device = device))
        }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Files(val files: PrefFiles) : UIState
    }
}