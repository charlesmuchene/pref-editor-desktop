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
import com.charlesmuchene.prefeditor.command.ListPrefFiles.PrefFilesResult
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.PrefFiles
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefEditScreen
import com.charlesmuchene.prefeditor.usecases.favorites.FavoritesUseCase
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
    private val favorites: FavoritesUseCase,
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
            result.isSuccess -> result.getOrNull()?.let(::map) ?: UIState.Error()
            else -> UIState.Error()
        }
    }

    fun fileSelected(prefFile: UIPrefFile) {
        launch {
            navigation.navigate(screen = PrefEditScreen(prefFile = prefFile.file, app = app, device = device))
        }
    }

    fun filter(input: String) {
        launch { _uiState.emit(UIState.Files(map(files.filter { it.name.contains(input, ignoreCase = true) }))) }
    }

    fun favorite(prefFile: UIPrefFile) {
        launch {
            if (prefFile.isFavorite) favorites.unfavoriteFile(file = prefFile.file, app = app, device = device)
            else favorites.favoriteFile(file = prefFile.file, app = app, device = device)
            _uiState.emit(UIState.Files(map(files)))
        }
    }

    private fun map(result: PrefFilesResult): UIState = when (result) {
        PrefFilesResult.EmptyFiles,
        PrefFilesResult.EmptyPrefs,
        -> UIState.Empty

        is PrefFilesResult.Files -> {
            this@PrefListingViewModel.files.addAll(result.files)
            UIState.Files(map(result.files))
        }

        PrefFilesResult.NonDebuggable -> UIState.Error(message = "Selected app is non-debuggable")
    }

    private fun map(files: PrefFiles): List<UIPrefFile> =
        files.map { file -> UIPrefFile(file = file, isFavorite = favorites.isFavorite(file)) }

    sealed interface UIState {
        data object Empty : UIState
        data object Loading : UIState
        data class Files(val files: List<UIPrefFile>) : UIState
        data class Error(val message: String? = null) : UIState
    }
}