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

import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.extensions.throttleLatest
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.models.UIApp
import com.charlesmuchene.prefeditor.navigation.FilesScreen
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AppListViewModel(
    reloadSignal: ReloadSignal,
    private val device: Device,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {
    private val processor = Processor()
    private val decoder = AppListDecoder()
    private val useCase = AppListUseCase(device = device, processor = processor, decoder = decoder)

    private var filter = ItemFilter.none

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _filtered = MutableSharedFlow<List<UIApp>>()
    val filtered: SharedFlow<List<UIApp>> = _filtered

    init {
        useCase.apps
            .onEach { _uiState.emit(mapToState(it)) }
            .launchIn(scope = scope)

        reloadSignal.signal
            .onEach { _uiState.emit(UIState.Loading) }
            .throttleLatest(delayMillis = 300)
            .onEach { useCase.fetch() }
            .drop(count = 1)
            .onEach { _message.emit("Apps reloaded") }
            .launchIn(scope = scope)
    }

    private fun mapToState(apps: Apps): UIState {
        return if (apps.isEmpty()) {
            UIState.Error
        } else {
            UIState.Apps(filter(filter = filter, apps = mapApps(apps)))
        }
    }

    private fun mapApps(apps: Apps): List<UIApp> =
        apps.map { app ->
            val isFavorite = favorites.isFavorite(app = app, device = device)
            UIApp(app = app, isFavorite = isFavorite)
        }

    /**
     * Select app
     *
     * @param app The selected [UIApp]
     */
    fun select(app: UIApp) {
        launch {
            navigation.navigate(screen = FilesScreen(app = app.app, device = device))
        }
    }

    /**
     * Filter content based on input
     *
     * Invoking this function with a value clears the filter
     * @param filter [ItemFilter]
     */
    fun filter(filter: ItemFilter) {
        this.filter = filter
        launch { _filtered.emit(filter(filter = filter, apps = mapApps(useCase.apps.value))) }
    }

    /**
     * Filter the given list of apps
     *
     * @param filter [ItemFilter] to apply
     * @param apps The [List] of [UIApp]s to filter
     * @return The filtered [List] of [UIApp]s
     */
    private fun filter(
        filter: ItemFilter,
        apps: List<UIApp>,
    ): ImmutableList<UIApp> =
        apps.filter { uiApp ->
            (if (filter.starred) uiApp.isFavorite else true) &&
                uiApp.app.packageName.contains(other = filter.text, ignoreCase = true)
        }.toImmutableList()

    /**
     * Un/Favorite an app
     *
     * @param app [UIApp] to un/favorite
     */
    suspend fun favorite(app: UIApp) =
        coroutineScope {
            async {
                if (app.isFavorite) {
                    favorites.unfavoriteApp(app = app.app, device = device)
                } else {
                    favorites.favoriteApp(app = app.app, device = device)
                }
                app.copy(isFavorite = !app.isFavorite)
            }.await()
        }

    sealed interface UIState {
        data object Error : UIState

        data object Loading : UIState

        data class Apps(val apps: ImmutableList<UIApp>) : UIState
    }
}
