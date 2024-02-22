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

package com.charlesmuchene.prefeditor.screens.device

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.charlesmuchene.prefeditor.models.UIDevice
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.providers.LocalReloadSignal
import com.charlesmuchene.prefeditor.resources.DevicesKey
import com.charlesmuchene.prefeditor.screens.device.DeviceListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.ListingScaffold
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.Toast
import com.charlesmuchene.prefeditor.ui.filter.FilterComponent
import com.charlesmuchene.prefeditor.ui.listing.ItemListing
import com.charlesmuchene.prefeditor.ui.listing.ItemRow
import com.charlesmuchene.prefeditor.ui.theme.Typography
import com.charlesmuchene.prefeditor.ui.theme.Typography.primary
import com.charlesmuchene.prefeditor.ui.theme.Typography.secondary
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.launch
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DeviceListScreen(modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val reloadSignal = LocalReloadSignal.current
    val scope = rememberCoroutineScope()
    val viewModel =
        remember {
            DeviceListViewModel(
                bundle = bundle,
                scope = scope,
                navigation = navigation,
                reloadSignal = reloadSignal,
                favorites = appState.favorites,
            )
        }
    val uiState by viewModel.uiState.collectAsState()

    val header = bundle[DevicesKey.ConnectedDevices]
    ListingScaffold(
        modifier = modifier,
        header = { Text(text = header, style = Typography.heading) },
        subHeader = { FilterComponent(placeholder = "Filter devices", onFilter = viewModel::filter) },
    ) {
        AnimatedContent(targetState = uiState, transitionSpec = { screenTransitionSpec() }) { state ->
            when (state) {
                UIState.Loading -> DeviceListLoading()
                is UIState.Error -> DeviceListError(state.message)
                UIState.NoDevices -> NoDevices()
                is UIState.Devices ->
                    if (state.devices.isEmpty()) {
                        NoFilterMatch()
                    } else {
                        DeviceList(devices = state.devices, viewModel = viewModel)
                    }
            }
        }
    }

    // TODO Collect similar values
    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DeviceList(
    devices: ImmutableList<UIDevice>,
    viewModel: DeviceListViewModel,
    modifier: Modifier = Modifier,
) {
    val filtered by viewModel.filtered.collectAsState(devices)

    if (filtered.isEmpty()) {
        NoFilterMatch(modifier = modifier)
    } else {
        ItemListing(modifier = modifier) {
            items(items = filtered, key = { it.device.serial }) { device ->
                DeviceRow(device = device, viewModel = viewModel, modifier = Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
private fun DeviceRow(
    device: UIDevice,
    viewModel: DeviceListViewModel,
    modifier: Modifier = Modifier,
) {
    val statusColor = viewModel.statusColor(device = device.device)
    var localDevice by remember(device) { mutableStateOf(device) }
    val scope = rememberCoroutineScope()

    ItemRow(
        item = localDevice,
        modifier = modifier,
        action = {
            when (it) {
                is ItemRowAction.Click<*> -> viewModel.select(it.item)
                is ItemRowAction.Favorite<*> -> scope.launch { localDevice = viewModel.favorite(it.item) }
            }
        },
    ) {
        Canvas(modifier = Modifier.size(APP_SPACING)) {
            drawCircle(color = statusColor)
        }
        Spacer(modifier = Modifier.width(APP_SPACING))
        Column(modifier = Modifier) {
            Text(text = localDevice.device.serial, style = primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = viewModel.formatDeviceAttributes(localDevice.device),
                style = secondary,
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DeviceListError(
    message: String,
    modifier: Modifier = Modifier,
) {
    FullScreenText(primary = message, modifier = modifier)
}

@Composable
private fun NoDevices(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[DevicesKey.EmptyDeviceList]
    FullScreenText(primary = primary, modifier = modifier)
}

@Composable
private fun NoFilterMatch(modifier: Modifier = Modifier) {
    FullScreenText(primary = "No devices matching filter", modifier = modifier)
}

@Composable
private fun DeviceListLoading(modifier: Modifier = Modifier) {
    val text = LocalBundle.current[DevicesKey.DeviceListLoading]
    Loading(text = text, modifier = modifier)
}
