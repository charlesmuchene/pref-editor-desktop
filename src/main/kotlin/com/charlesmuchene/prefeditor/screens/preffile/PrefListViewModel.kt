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
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.PrefFiles
import com.charlesmuchene.prefeditor.extensions.throttleLatest
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.navigation.EditScreen
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListUseCase.FetchStatus
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class PrefListViewModel(
    executable: String,
    reloadSignal: ReloadSignal,
    private val app: App,
    private val device: Device,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {
    private val processor = Processor()
    private val decoder = PrefFileListDecoder()
    private val commands = PrefFileListCommand.create(app = app, device = device, executable = executable)
    private val useCase = PrefFileListUseCase(commands = commands, processor = processor, decoder = decoder)

    private var filter = ItemFilter.none

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _filtered = MutableSharedFlow<List<UIPrefFile>>()
    val filtered: SharedFlow<List<UIPrefFile>> = _filtered.asSharedFlow()

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    init {
        scope.launch {
            useCase.status
                .map(::mapToState)
                .collect(_uiState)
        }

        reloadSignal.signal
            .onEach { _uiState.emit(UIState.Loading) }
            .throttleLatest(delayMillis = 300)
            .onEach { useCase.fetch() }
            .drop(count = 1)
            .onEach { _message.emit("Files reloaded") }
            .launchIn(scope = scope)
    }

    private fun mapToState(fetchStatus: FetchStatus): UIState =
        when (fetchStatus) {
            is FetchStatus.Error -> UIState.Error(fetchStatus.message)
            FetchStatus.Fetching -> UIState.Loading
            is FetchStatus.Done ->
                when (val result = fetchStatus.result) {
                    PrefFileResult.NoFiles -> UIState.Empty

                    is PrefFileResult.Files -> UIState.Files(filter(filter = filter, files = map(result.files)))

                    PrefFileResult.NonDebuggable -> UIState.Error(message = "Selected app is non-debuggable")
                }
        }

    private fun map(files: PrefFiles): List<UIPrefFile> =
        files.map { file -> UIPrefFile(file = file, isFavorite = favorites.isFavorite(file, app, device)) }

    /**
     * Select a file
     *
     * Note: Datastore preference files are readonly for now
     *
     * @param file Selected [UIPrefFile]
     * @param readOnly Determine if we should show readonly view or editor.
     */
    fun select(
        file: UIPrefFile,
        readOnly: Boolean = false,
    ) {
        launch {
            val onlyRead = if (file.file.type == PrefFile.Type.DATA_STORE) true else readOnly
            navigation.navigate(screen = EditScreen(file = file.file, app = app, device = device, readOnly = onlyRead))
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
            val status = useCase.status.value
            if (status is FetchStatus.Done) {
                val result = status.result
                if (result is PrefFileResult.Files) {
                    _filtered.emit(filter(filter = filter, files = map(result.files)))
                }
            }
        }
    }

    /**
     * Filter the given list of files
     *
     * @param filter [ItemFilter] to apply
     * @param files The [List] of [UIPrefFile]s to filter
     * @return The filtered [List] of [UIPrefFile]s
     */
    private fun filter(
        filter: ItemFilter,
        files: List<UIPrefFile>,
    ): ImmutableList<UIPrefFile> =
        files.filter { uiFile ->
            (if (filter.starred) uiFile.isFavorite else true) &&
                    uiFile.file.name.contains(other = filter.text, ignoreCase = true)
        }.toImmutableList()

    /**
     * Un/Favorite a file
     *
     * @param file [UIPrefFile] to un/favorite
     */
    suspend fun favorite(file: UIPrefFile): UIPrefFile {
        val status = useCase.status.value
        if (status !is FetchStatus.Done || status.result !is PrefFileResult.Files) return file
        if (file.isFavorite) {
            favorites.unfavoriteFile(file = file.file, app = app, device = device)
        } else {
            favorites.favoriteFile(file = file.file, app = app, device = device)
        }
        return file.copy(isFavorite = !file.isFavorite)
    }

    sealed interface UIState {
        data object Empty : UIState

        data object Loading : UIState

        data class Files(val files: ImmutableList<UIPrefFile>) : UIState

        data class Error(val message: String? = null) : UIState
    }
}
