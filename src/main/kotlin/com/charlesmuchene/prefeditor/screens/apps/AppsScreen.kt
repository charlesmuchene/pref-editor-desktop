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

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Apps
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.DeviceKey
import com.charlesmuchene.prefeditor.screens.apps.AppsScreenViewModel.UIState
import com.charlesmuchene.prefeditor.ui.Listing
import com.charlesmuchene.prefeditor.ui.ListingRow
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AppsScreen(device: Device, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val navigation = LocalNavigation.current
    val viewModel = remember {
        AppsScreenViewModel(bridge = bridge, scope = scope, device = device, navigation = navigation)
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        UIState.Loading -> LoadingApps(modifier = modifier)
        UIState.Error -> LoadingAppError(modifier = modifier)
        is UIState.Apps -> AppListing(apps = (state as UIState.Apps).apps, modifier = modifier, viewModel = viewModel)
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

@Composable
private fun AppListing(apps: Apps, modifier: Modifier = Modifier, viewModel: AppsScreenViewModel) {
    val header = LocalBundle.current[DeviceKey.AppListingTitle]
    Listing(header = header, filterPlaceholder = "Filter apps", modifier = modifier, onFilter = viewModel::filter) {
        if (apps.isEmpty()) item { Text(text = "No apps matching filter", style = Typography.primary) }
        items(items = apps, key = App::packageName) { app ->
            AppRow(app = app, onClick = viewModel::appSelected)
        }
    }
}

@Composable
private fun AppRow(app: App, modifier: Modifier = Modifier, onClick: (App) -> Unit) {
    ListingRow(item = app, modifier = modifier, onClick = onClick) {
        Text(text = app.packageName, style = Typography.primary, modifier = Modifier.padding(4.dp))
    }
}