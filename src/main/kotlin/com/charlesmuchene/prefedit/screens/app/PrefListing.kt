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

package com.charlesmuchene.prefedit.screens.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.PrefFile
import com.charlesmuchene.prefedit.data.PrefFiles
import com.charlesmuchene.prefedit.providers.LocalAppState
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.AppKey
import com.charlesmuchene.prefedit.screens.app.PrefListingViewModel.UIState
import com.charlesmuchene.prefedit.ui.Listing
import com.charlesmuchene.prefedit.ui.ListingRow
import com.charlesmuchene.prefedit.ui.Loading
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.theme.PrefEditTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PrefListing(app: App, device: Device, modifier: Modifier = Modifier) {
    val bridge = LocalBridge.current
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PrefListingViewModel(app = app, device = device, bridge = bridge, scope = scope, appState = appState)
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        UIState.Error -> PrefListingError(modifier = modifier)
        UIState.Loading -> PrefListingLoading(modifier = modifier)
        is UIState.Files -> PrefListingSuccess(
            modifier = modifier,
            viewModel = viewModel,
            prefFiles = (state as UIState.Files).files,
        )
    }
}

@Composable
private fun PrefListingError(modifier: Modifier = Modifier) {
    // TODO Add title, which device, for which app
    SingleText(key = AppKey.PrefListingError, modifier = modifier)
}

@Composable
private fun PrefListingLoading(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[AppKey.PrefListingLoading]
    Loading(text = text, modifier = modifier)
}

@Composable
private fun PrefListingSuccess(prefFiles: PrefFiles, modifier: Modifier = Modifier, viewModel: PrefListingViewModel) {
    val bundle = LocalBundle.current
    if (prefFiles.isEmpty()) SingleText(key = AppKey.PrefListingEmpty, modifier = modifier)
    else Listing(header = bundle[AppKey.PrefListingTitle], modifier = modifier) {
        items(items = prefFiles, key = PrefFile::name) { prefFile ->
            PrefListingRow(prefFile = prefFile, onClick = viewModel::fileSelected)
        }
    }
}

@Composable
private fun PrefListingRow(prefFile: PrefFile, modifier: Modifier = Modifier, onClick: (PrefFile) -> Unit) {
    ListingRow(item = prefFile, onClick = onClick) {
        Column(modifier = modifier.padding(4.dp)) {
            Text(text = prefFile.name, style = PrefEditTextStyle.primary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = prefFile.type.text, style = PrefEditTextStyle.secondary, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}