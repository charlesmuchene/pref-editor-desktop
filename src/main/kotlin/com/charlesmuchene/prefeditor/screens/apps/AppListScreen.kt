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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.models.UIApp
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.DeviceKey
import com.charlesmuchene.prefeditor.screens.apps.AppListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.*
import com.charlesmuchene.prefeditor.ui.theme.Typography
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AppListScreen(device: Device, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val viewModel = remember {
        AppListViewModel(
            bridge = bridge,
            scope = scope,
            device = device,
            navigation = navigation,
            favorites = appState.favorites,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    val header = LocalBundle.current[DeviceKey.AppListingTitle]
    Scaffolding(
        modifier = modifier,
        header = { Text(text = header, style = Typography.heading) },
        subHeader = {
            FilterRow(placeholder = "Filter apps", onFilter = viewModel::filter)
        }
    ) {
        updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
            when (state) {
                UIState.Loading -> LoadingApps(modifier = modifier)
                UIState.Error -> LoadingAppError(modifier = modifier)
                is UIState.Apps -> if (state.apps.isEmpty()) NoFilterMatch(modifier = modifier)
                else AppListing(apps = state.apps, modifier = modifier, viewModel = viewModel)
            }
        }
    }
}

@Composable
private fun LoadingAppError(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[DeviceKey.AppListingError]
    FullScreenText(primary = text, modifier = modifier)
}

@Composable
private fun LoadingApps(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[DeviceKey.AppListingLoading]
    Loading(text = text, modifier = modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppListing(apps: List<UIApp>, modifier: Modifier = Modifier, viewModel: AppListViewModel) {
    val filtered by viewModel.filtered.collectAsState(apps)

    ItemListing(modifier = modifier) {
        items(items = filtered, key = { it.app.packageName }) { app ->
            AppRow(app = app, viewModel = viewModel, modifier = Modifier.animateItemPlacement())
        }
    }
}

@Composable
private fun AppRow(app: UIApp, modifier: Modifier = Modifier, viewModel: AppListViewModel) {
    val scope = rememberCoroutineScope()
    var localApp by remember(app) { mutableStateOf(app) }

    ItemRow(
        item = localApp,
        modifier = modifier,
        onClick = viewModel::selected,
        onFavorite = { scope.launch { localApp = viewModel.favorite(it) } }
    ) {
        Text(text = localApp.app.packageName, style = Typography.primary, modifier = Modifier.padding(4.dp))
    }
}

@Composable
private fun NoFilterMatch(modifier: Modifier = Modifier) {
    FullScreenText(primary = "No apps matching filter", modifier = modifier)
}