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
import com.charlesmuchene.prefeditor.data.PrefFiles
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefEditScreen
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrefListViewModel(
    private val app: App,
    private val device: Device,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {

    private val decoder = PrefFileListDecoder()
    private val processor = Processor()
    private val useCase = PrefFileListUseCase(app = app, device = device, processor = processor, decoder = decoder)

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        useCase.fileResult.onEach { _uiState.emit(mapToState(it)) }.launchIn(scope = scope)
        launch { useCase.list() }
    }

    private fun mapToState(result: PrefFileResult): UIState = when (result) {
        PrefFileResult.EmptyFiles,
        PrefFileResult.EmptyPrefs,
        -> UIState.Empty

        is PrefFileResult.Files -> UIState.Files(map(result.files))

        PrefFileResult.NonDebuggable -> UIState.Error(message = "Selected app is non-debuggable")
    }

    private fun map(files: PrefFiles): List<UIPrefFile> =
        files.map { file -> UIPrefFile(file = file, isFavorite = favorites.isFavorite(file, app, device)) }

    fun fileSelected(prefFile: UIPrefFile) {
        launch {
            navigation.navigate(screen = PrefEditScreen(prefFile = prefFile.file, app = app, device = device))
        }
    }

    fun filter(input: String) {
        launch {
            val result = useCase.fileResult.value
            if (result is PrefFileResult.Files) {
                val files = result.files.filter { it.name.contains(input, ignoreCase = true) }
                val state = UIState.Files(map(files))
                _uiState.emit(state)
            }
        }
    }

    fun favorite(prefFile: UIPrefFile) {
        launch {
            val result = useCase.fileResult.value
            if (result is PrefFileResult.Files) {
                if (prefFile.isFavorite) favorites.unfavoriteFile(file = prefFile.file, app = app, device = device)
                else favorites.favoriteFile(file = prefFile.file, app = app, device = device)
                val state = UIState.Files(map(result.files))
                _uiState.emit(state)
            }
        }
    }

    sealed interface UIState {
        data object Empty : UIState
        data object Loading : UIState
        data class Files(val files: List<UIPrefFile>) : UIState
        data class Error(val message: String? = null) : UIState
    }
}