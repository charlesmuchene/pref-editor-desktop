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

package com.charlesmuchene.prefeditor.screens.preffile

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.AppKey
import com.charlesmuchene.prefeditor.screens.preffile.PrefListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.*
import com.charlesmuchene.prefeditor.ui.theme.Typography
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

@Composable
fun PrefFileListScreen(app: App, device: Device, modifier: Modifier = Modifier) {
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PrefListViewModel(
            app = app,
            device = device,
            scope = scope,
            navigation = navigation,
            favorites = appState.favorites,
        )
    }
    val uiState by viewModel.uiState.collectAsState()

    val header = LocalBundle.current[AppKey.PrefListingTitle]
    Scaffolding(
        modifier = modifier,
        header = { Text(text = header, style = Typography.heading) },
        subHeader = {
            FilterRow(placeholder = "Filter files", onFilter = viewModel::filter, onClear = viewModel::filter)
        }) {
        updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
            when (state) {
                UIState.Empty -> PrefListingEmpty(modifier = modifier)
                UIState.Loading -> PrefListingLoading(modifier = modifier)
                is UIState.Error -> PrefListingError(modifier = modifier, message = state.message)
                is UIState.Files -> if (state.files.isEmpty()) NoFilterMatch(modifier = modifier)
                else PrefListingSuccess(modifier = modifier, viewModel = viewModel, prefFiles = state.files)
            }
        }
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
    viewModel: PrefListViewModel,
    modifier: Modifier = Modifier,
) {
    ItemListing(modifier = modifier) {
        items(items = prefFiles, key = { it.file.name }) { prefFile ->
            PrefListingRow(prefFile = prefFile, viewModel = viewModel)
        }
    }
}

@Composable
private fun PrefListingRow(prefFile: UIPrefFile, modifier: Modifier = Modifier, viewModel: PrefListViewModel) {
    val scope = rememberCoroutineScope()
    var localPref by remember(prefFile) { mutableStateOf(prefFile) }

    ItemRow(
        item = localPref,
        onClick = viewModel::fileSelected,
        onFavorite = { scope.launch { localPref = viewModel.favorite(it) } }
    ) {
        Column(modifier = modifier.padding(4.dp)) {
            Text(text = localPref.file.name, style = Typography.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = localPref.file.type.text, style = Typography.secondary, color = JewelTheme.contentColor)
        }
    }
}

@Composable
private fun NoFilterMatch(modifier: Modifier = Modifier) {
    FullScreenText(primary = "No preference files matching filter", modifier = modifier)
}