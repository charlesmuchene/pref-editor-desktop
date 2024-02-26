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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.models.ItemRowAction
import com.charlesmuchene.prefeditor.models.UIApp
import com.charlesmuchene.prefeditor.navigation.AppsScreen
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.providers.LocalReloadSignal
import com.charlesmuchene.prefeditor.resources.AppsKey
import com.charlesmuchene.prefeditor.screens.apps.AppListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.ListingScaffold
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.filter.FilterComponent
import com.charlesmuchene.prefeditor.ui.listing.ItemListing
import com.charlesmuchene.prefeditor.ui.listing.ItemRow
import com.charlesmuchene.prefeditor.ui.theme.Typography
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AppListScreen(
    screen: AppsScreen,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val reloadSignal = LocalReloadSignal.current
    val viewModel =
        remember {
            AppListViewModel(
                scope = scope,
                device = screen.device,
                navigation = navigation,
                reloadSignal = reloadSignal,
                favorites = appState.favorites,
                executable = appState.executable,
            )
        }
    val uiState by viewModel.uiState.collectAsState()

    val header = LocalBundle.current[AppsKey.AppListingTitle]
    ListingScaffold(
        modifier = modifier,
        header = { Text(text = header, style = Typography.heading) },
        subHeader = {
            FilterComponent(placeholder = "Filter apps", onFilter = viewModel::filter)
        },
    ) {
        updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
            when (state) {
                UIState.Loading -> LoadingApps()
                UIState.Error -> LoadingAppError()
                is UIState.Apps ->
                    if (state.apps.isEmpty()) {
                        NoFilterMatch()
                    } else {
                        AppListing(apps = state.apps, viewModel = viewModel)
                    }
            }
        }
    }
}

@Composable
private fun LoadingAppError(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[AppsKey.AppListingError]
    FullScreenText(primary = text, modifier = modifier)
}

@Composable
private fun LoadingApps(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[AppsKey.AppListingLoading]
    Loading(text = text, modifier = modifier)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AppListing(
    apps: ImmutableList<UIApp>,
    viewModel: AppListViewModel,
    modifier: Modifier = Modifier,
) {
    val filtered by viewModel.filtered.collectAsState(apps)

    if (filtered.isEmpty()) {
        NoFilterMatch(modifier = modifier)
    } else {
        ItemListing(modifier = modifier) {
            items(items = filtered, key = { it.app.packageName }) { app ->
                AppRow(app = app, viewModel = viewModel, modifier = Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
private fun AppRow(
    app: UIApp,
    viewModel: AppListViewModel,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var localApp by remember(app) { mutableStateOf(app) }

    ItemRow(
        item = localApp,
        modifier = modifier,
        action = {
            when (it) {
                is ItemRowAction.Click<*> -> viewModel.select(it.item)
                is ItemRowAction.Favorite<*> -> scope.launch { localApp = viewModel.favorite(it.item) }
            }
        },
    ) {
        Text(text = localApp.app.packageName, style = Typography.primary, modifier = Modifier.padding(4.dp))
    }
}

@Composable
private fun NoFilterMatch(modifier: Modifier = Modifier) {
    FullScreenText(primary = "No apps matching filter", modifier = modifier)
}
