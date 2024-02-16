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
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.models.ItemRowAction
import com.charlesmuchene.prefeditor.models.UIPrefFile
import com.charlesmuchene.prefeditor.navigation.FilesScreen
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.providers.LocalReloadSignal
import com.charlesmuchene.prefeditor.resources.AppKey
import com.charlesmuchene.prefeditor.screens.preffile.PrefListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.APP_ICON_BUTTON_SIZE
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import com.charlesmuchene.prefeditor.ui.BreathingContainer
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.ListingScaffold
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.Toast
import com.charlesmuchene.prefeditor.ui.filter.FilterComponent
import com.charlesmuchene.prefeditor.ui.listing.ItemListing
import com.charlesmuchene.prefeditor.ui.listing.ItemRow
import com.charlesmuchene.prefeditor.ui.theme.Typography
import com.charlesmuchene.prefeditor.ui.theme.mustard
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun FileListScreen(
    screen: FilesScreen,
    modifier: Modifier = Modifier,
) {
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val reloadSignal = LocalReloadSignal.current
    val scope = rememberCoroutineScope()
    val viewModel =
        remember {
            PrefListViewModel(
                scope = scope,
                app = screen.app,
                device = screen.device,
                navigation = navigation,
                reloadSignal = reloadSignal,
                favorites = appState.favorites,
            )
        }
    val uiState by viewModel.uiState.collectAsState()

    val header = LocalBundle.current[AppKey.PrefListingTitle]
    ListingScaffold(
        modifier = modifier,
        header = { Text(text = header, style = Typography.heading) },
        subHeader = {
            FilterComponent(placeholder = "Filter files", onFilter = viewModel::filter)
        },
    ) {
        updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
            when (state) {
                UIState.Empty -> PrefListingEmpty()
                UIState.Loading -> PrefListingLoading()
                is UIState.Error -> PrefListingError(message = state.message)
                is UIState.Files ->
                    if (state.files.isEmpty()) {
                        NoFilterMatch()
                    } else {
                        PrefListingSuccess(viewModel = viewModel, prefFiles = state.files)
                    }
            }
        }
    }

    // TODO Collect similar values
    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun PrefListingEmpty(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[AppKey.PrefListingEmpty]
    FullScreenText(primary = primary, modifier = modifier)
}

@Composable
private fun PrefListingError(
    modifier: Modifier = Modifier,
    message: String? = null,
) {
    val primary = LocalBundle.current[AppKey.PrefListingError]
    FullScreenText(primary = primary, secondary = message, modifier = modifier)
}

@Composable
private fun PrefListingLoading(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[AppKey.PrefListingLoading]
    Loading(text = text, modifier = modifier)
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun PrefListingSuccess(
    prefFiles: ImmutableList<UIPrefFile>,
    viewModel: PrefListViewModel,
    modifier: Modifier = Modifier,
) {
    val filtered by viewModel.filtered.collectAsState(prefFiles)

    if (filtered.isEmpty()) {
        NoFilterMatch(modifier = modifier)
    } else {
        ItemListing(modifier = modifier) {
            items(items = filtered, key = { it.file.name }) { prefFile ->
                PrefListingRow(prefFile = prefFile, viewModel = viewModel, modifier = Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
private fun PrefListingRow(
    prefFile: UIPrefFile,
    viewModel: PrefListViewModel,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    var localPref by remember(prefFile) { mutableStateOf(prefFile) }

    ItemRow(
        item = localPref,
        modifier = modifier,
        action = {
            when (it) {
                is ItemRowAction.Click<*> -> viewModel.select(it.item)
                is ItemRowAction.Favorite<*> -> scope.launch { localPref = viewModel.favorite(it.item) }
            }
        },
        endItems = { ViewFileButton(viewModel, prefFile) },
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            Text(text = localPref.file.name, style = Typography.primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = localPref.file.type.text, style = Typography.secondary, color = JewelTheme.contentColor)
        }
        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun ViewFileButton(
    viewModel: PrefListViewModel,
    prefFile: UIPrefFile,
    modifier: Modifier = Modifier,
) {
    BreathingContainer(modifier = modifier) {
        IconButton(
            modifier = Modifier.size(APP_ICON_BUTTON_SIZE).clip(CircleShape),
            onClick = { viewModel.select(file = prefFile, readOnly = true) },
        ) {
            val painter by rememberIconPainter(name = "read")
            val tint = if (LocalAppState.current.theme.isDark()) mustard else JewelTheme.contentColor
            Icon(
                tint = tint,
                painter = painter,
                contentDescription = "View file",
                modifier =
                    Modifier
                        .size(APP_SPACING)
                        .pointerOnHover(),
            )
        }
    }
}

@Composable
private fun NoFilterMatch(modifier: Modifier = Modifier) {
    FullScreenText(primary = "No preference files matching filter", modifier = modifier)
}
