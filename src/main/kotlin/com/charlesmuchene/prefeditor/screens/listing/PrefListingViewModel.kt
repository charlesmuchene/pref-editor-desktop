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

package com.charlesmuchene.prefeditor.screens.listing

import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.command.ListPrefFiles
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.PrefFiles
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefEditScreen
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

    private val files = mutableListOf<PrefFile>()
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        launch { _uiState.emit(getPrefFiles()) }
    }

    private suspend fun getPrefFiles(): UIState {
        val result = bridge.execute(command = ListPrefFiles(app = app, device = device))
        return when {
            result.isSuccess -> result.getOrNull()?.let { prefFiles ->
                if (prefFiles.isEmpty()) UIState.Empty
                else UIState.Files(prefFiles).also {
                    this.files.addAll(it.files)
                }
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    fun fileSelected(prefFile: PrefFile) {
        launch {
            navigation.navigate(screen = PrefEditScreen(prefFile = prefFile, app = app, device = device))
        }
    }

    fun filter(input: String) {
        launch { _uiState.emit(UIState.Files(files.filter { it.name.contains(input, ignoreCase = true) })) }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Empty : UIState
        data object Loading : UIState
        data class Files(val files: PrefFiles) : UIState
    }
}