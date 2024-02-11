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
import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.extensions.useCaseTransform
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.models.UIApp
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.navigation.PrefListScreen
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class AppListViewModel(
    private val device: Device,
    private val bridge: Bridge,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {

    private val processor = Processor()
    private val decoder = AppListDecoder()
    private val useCase = AppListUseCase(device = device, processor = processor, decoder = decoder)

    private var filter = ItemFilter.none
    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()
    private val _filtered = MutableSharedFlow<List<UIApp>>()
    val filtered: SharedFlow<List<UIApp>> = _filtered

    init {
        useCase.apps.useCaseTransform().onEach { _uiState.emit(mapToState(it)) }.launchIn(scope = scope)
        launch { useCase.list() }
    }

    private fun mapToState(apps: Apps): UIState {
        return if (apps.isEmpty()) UIState.Error
        else UIState.Apps(filter(filter = filter, apps = mapApps(apps)))
    }

    private fun mapApps(apps: Apps): List<UIApp> = apps.map { app ->
        val isFavorite = favorites.isFavorite(app = app, device = device)
        UIApp(app = app, isFavorite = isFavorite)
    }

    /**
     * App selected
     *
     * @param app The selected [UIApp]
     */
    fun selected(app: UIApp) {
        launch {
            navigation.navigate(screen = PrefListScreen(app = app.app, device = device))
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
    private fun filter(filter: ItemFilter, apps: List<UIApp>) = apps.filter { uiApp ->
        (if (filter.starred) uiApp.isFavorite else true) &&
                uiApp.app.packageName.contains(other = filter.text, ignoreCase = true)
    }

    /**
     * Un/Favorite an app
     *
     * @param app [UIApp] to un/favorite
     */
    suspend fun favorite(app: UIApp) = coroutineScope {
        async {
            if (app.isFavorite) favorites.unfavoriteApp(app = app.app, device = device)
            else favorites.favoriteApp(app = app.app, device = device)
            app.copy(isFavorite = !app.isFavorite)
        }.await()
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Apps(val apps: List<UIApp>) : UIState
    }
}