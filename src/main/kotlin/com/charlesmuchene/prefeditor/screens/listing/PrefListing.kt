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

package com.charlesmuchene.prefeditor.screens.listing

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.extensions.OnFavorite
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.AppKey
import com.charlesmuchene.prefeditor.screens.listing.PrefListingViewModel.UIState
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.Listing
import com.charlesmuchene.prefeditor.ui.ListingRow
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PrefListing(app: App, device: Device, modifier: Modifier = Modifier) {
    val bridge = LocalBridge.current
    val navigation = LocalNavigation.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PrefListingViewModel(
            app = app,
            device = device,
            bridge = bridge,
            scope = scope,
            navigation = navigation,
        )
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        UIState.Empty -> PrefListingEmpty(modifier = modifier)
        is UIState.Error -> PrefListingError(modifier = modifier, message = (state as UIState.Error).message)
        UIState.Loading -> PrefListingLoading(modifier = modifier)
        is UIState.Files -> PrefListingSuccess(
            modifier = modifier,
            viewModel = viewModel,
            prefFiles = (state as UIState.Files).files,
        )
    }
}

@Composable
private fun PrefListingEmpty(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[AppKey.PrefListingEmpty]
    FullScreenText(primary = primary, modifier = modifier)
}

@Composable
private fun PrefListingError(modifier: Modifier = Modifier, message: String? = null) {
    val primary = LocalBundle.current[AppKey.PrefListingError]
    FullScreenText(primary = primary, secondary = message, modifier = modifier)
}

@Composable
private fun PrefListingLoading(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[AppKey.PrefListingLoading]
    Loading(text = text, modifier = modifier)
}

@Composable
private fun PrefListingSuccess(
    prefFiles: List<UIPrefFile>,
    modifier: Modifier = Modifier,
    viewModel: PrefListingViewModel,
) {
    val header = LocalBundle.current[AppKey.PrefListingTitle]
    Listing(
        header = header,
        filterPlaceholder = "Filter preferences",
        modifier = modifier,
        onFilter = viewModel::filter
    ) {
        if (prefFiles.isEmpty()) item { Text(text = "No preferences matching filter", style = Typography.primary) }
        items(items = prefFiles, key = { it.file.name }) { prefFile ->
            PrefListingRow(prefFile = prefFile, onClick = viewModel::fileSelected, onFavorite = viewModel::favorite)
        }
    }
}

@Composable
private fun PrefListingRow(
    prefFile: UIPrefFile,
    modifier: Modifier = Modifier,
    onClick: (UIPrefFile) -> Unit,
    onFavorite: OnFavorite<UIPrefFile>,
) {
    ListingRow(item = prefFile, onClick = onClick, onFavorite = onFavorite) {
        Column(modifier = modifier.padding(4.dp)) {
            Text(text = prefFile.file.name, style = Typography.primary)
            Spacer(modifier = Modifier.height(2.dp))
            Text(text = prefFile.file.type.text, style = Typography.secondary, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(2.dp))
        }
    }
}