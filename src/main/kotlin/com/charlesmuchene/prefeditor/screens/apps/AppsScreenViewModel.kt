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

import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.command.ListApps
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.favorites.Favorite
import com.charlesmuchene.prefeditor.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.models.UIApp
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
    private val appState: AppState,
    private val favorites: FavoritesUseCase = FavoritesUseCase(appState.preferences),
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
                else UIState.Apps(mapApps(apps)).also {
                    this.apps.addAll(it.apps.map(UIApp::app))
                }
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    private fun mapApps(apps: Apps): List<UIApp> = apps.map { app ->
        val isFavorite = favorites.isFavorite(app)
        UIApp(app = app, isFavorite = isFavorite)
    }

    fun appSelected(app: UIApp) {
        launch {
            navigation.navigate(screen = PrefListScreen(app = app.app, device = device))
        }
    }

    fun filter(input: String) {
        launch {
            _uiState.emit(UIState.Apps(mapApps(apps.filter { app ->
                app.packageName.contains(other = input, ignoreCase = true)
            })))
        }
    }

    fun onFavorite(app: UIApp) {
        launch { favorites.favoriteApp(app.app, device) }
    }

    sealed interface UIState {
        data object Error : UIState
        data object Loading : UIState
        data class Apps(val apps: List<UIApp>) : UIState
    }
}