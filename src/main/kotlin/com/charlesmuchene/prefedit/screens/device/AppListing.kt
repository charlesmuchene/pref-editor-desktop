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

package com.charlesmuchene.prefedit.screens.device

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Apps
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.providers.LocalAppState
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.DeviceKey
import com.charlesmuchene.prefedit.screens.device.AppListingViewModel.UIState
import com.charlesmuchene.prefedit.ui.Listing
import com.charlesmuchene.prefedit.ui.ListingRow
import com.charlesmuchene.prefedit.ui.Loading
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.theme.PrefEditTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AppsScreen(device: Device, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val appState = LocalAppState.current
    val viewModel = remember {
        AppListingViewModel(bridge = bridge, scope = scope, device = device, appState = appState)
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
    SingleText(key = DeviceKey.AppListingError, modifier = modifier)
}

@Composable
private fun LoadingApps(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[DeviceKey.AppListingLoading]
    Loading(text = text, modifier = modifier)
}

@Composable
private fun AppListing(apps: Apps, modifier: Modifier = Modifier, viewModel: AppListingViewModel) {
    val header = LocalBundle.current[DeviceKey.AppListingTitle]
    Listing(header = header, modifier = modifier) {
        items(items = apps, key = App::packageName) { app ->
            AppRow(app = app, onClick = viewModel::appSelected)
        }
    }
}

@Composable
private fun AppRow(app: App, modifier: Modifier = Modifier, onClick: (App) -> Unit) {
    ListingRow(item = app, modifier = modifier, onClick = onClick) {
        Text(text = app.packageName, style = PrefEditTextStyle.primary, modifier = Modifier.padding(4.dp))
    }
}