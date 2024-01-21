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

import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Apps
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.DeviceKey
import com.charlesmuchene.prefedit.screens.device.AppListingViewModel.UIState
import com.charlesmuchene.prefedit.ui.Listing
import com.charlesmuchene.prefedit.ui.Loading
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.theme.PrefEditFonts
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AppScreen(modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val viewModel = remember { AppListingViewModel(bridge = bridge, scope = scope) }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is UIState.Apps -> AppListing(apps = (state as UIState.Apps).apps, modifier = modifier)
        UIState.Error -> LoadingAppError(modifier = modifier)
        UIState.Loading -> LoadingApps(modifier = modifier)
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
private fun AppListing(apps: Apps, modifier: Modifier = Modifier) {
    val header = LocalBundle.current[DeviceKey.AppListingTitle]
    Listing(items = apps, header = header, modifier = modifier) {
        items(items = apps, key = { App::packageName }) { app ->
            AppRow(app = app)
        }
    }
}

@Composable
private fun AppRow(app: App, modifier: Modifier = Modifier) {
    Text(text = app.packageName, style = PrefEditFonts.primary, modifier = modifier)
}