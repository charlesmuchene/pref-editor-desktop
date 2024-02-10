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
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefEditScreen
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class PrefListViewModel(
    private val app: App,
    private val device: Device,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {

    private val processor = Processor()
    private val decoder = PrefFileListDecoder()
    private val useCase = PrefFileListUseCase(app = app, device = device, processor = processor, decoder = decoder)

    private var filter = ItemFilter.none

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _filtered = MutableSharedFlow<List<UIPrefFile>>()
    val filtered: SharedFlow<List<UIPrefFile>> = _filtered.asSharedFlow()

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    init {
        useCase.fileResult.onEach { _uiState.emit(mapToState(it)) }.launchIn(scope = scope)
        launch { useCase.list() }
    }

    private fun mapToState(result: PrefFileResult): UIState = when (result) {
        PrefFileResult.EmptyFiles,
        PrefFileResult.EmptyPrefs,
        -> UIState.Empty

        is PrefFileResult.Files -> UIState.Files(filter(filter = filter, files = map(result.files)))

        PrefFileResult.NonDebuggable -> UIState.Error(message = "Selected app is non-debuggable")
    }

    private fun map(files: PrefFiles): List<UIPrefFile> =
        files.map { file -> UIPrefFile(file = file, isFavorite = favorites.isFavorite(file, app, device)) }

    /**
     * Select a file
     *
     * @param file Selected [UIPrefFile]
     */
    fun selected(file: UIPrefFile) {
        launch {
            navigation.navigate(screen = PrefEditScreen(prefFile = file.file, app = app, device = device))
        }
    }

    /**
     * Filter content based on input
     *
     * Invoking this function with a value clears the filter
     * @param filter [ItemFilter] to apply
     */
    fun filter(filter: ItemFilter) {
        this.filter = filter
        launch {
            val result = useCase.fileResult.value
            if (result is PrefFileResult.Files) _filtered.emit(filter(filter = filter, files = map(result.files)))
        }
    }

    /**
     * Filter the given list of files
     *
     * @param filter [ItemFilter] to apply
     * @param files The [List] of [UIPrefFile]s to filter
     * @return The filtered [List] of [UIPrefFile]s
     */
    private fun filter(filter: ItemFilter, files: List<UIPrefFile>) = files.filter { uiFile ->
        (if (filter.starred) uiFile.isFavorite else true) &&
                uiFile.file.name.contains(other = filter.text, ignoreCase = true)
    }

    /**
     * Un/Favorite a file
     *
     * @param file [UIPrefFile] to un/favorite
     */
    suspend fun favorite(file: UIPrefFile) = coroutineScope {
        val result = useCase.fileResult.value
        if (result !is PrefFileResult.Files) file
        else async {
            if (file.isFavorite) favorites.unfavoriteFile(file = file.file, app = app, device = device)
            else favorites.favoriteFile(file = file.file, app = app, device = device)
            file.copy(isFavorite = !file.isFavorite)
        }.await()
    }

    sealed interface UIState {
        data object Empty : UIState
        data object Loading : UIState
        data class Files(val files: List<UIPrefFile>) : UIState
        data class Error(val message: String? = null) : UIState
    }
}