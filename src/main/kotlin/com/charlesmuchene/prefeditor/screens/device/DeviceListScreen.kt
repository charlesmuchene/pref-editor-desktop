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
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.models.UIDevice
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.HomeKey
import com.charlesmuchene.prefeditor.screens.device.DeviceListViewModel.UIState
import com.charlesmuchene.prefeditor.ui.*
import com.charlesmuchene.prefeditor.ui.theme.Typography.primary
import com.charlesmuchene.prefeditor.ui.theme.Typography.secondary
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DeviceListScreen(modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        DeviceListViewModel(
            bundle = bundle,
            scope = scope,
            navigation = navigation,
            favorites = appState.favorites,
        )
    }
    val uiState by viewModel.uiState.collectAsState()
    updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
        when (state) {
            UIState.Error -> DeviceListError(modifier = modifier)
            UIState.NoDevices -> NoDevices(modifier = modifier)
            is UIState.Devices -> DeviceList(
                devices = state.devices,
                viewModel = viewModel,
                modifier = modifier,
            )
        }
    }

    // TODO Collect similar values
    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun DeviceList(devices: List<UIDevice>, viewModel: DeviceListViewModel, modifier: Modifier = Modifier) {
    val header = LocalBundle.current[HomeKey.ConnectedDevices]

    ItemListing(
        header = header,
        filterPlaceholder = "Filter devices",
        modifier = modifier,
        onFilter = viewModel::filter
    ) {
        if (devices.isEmpty()) item { Text(text = "No devices matching filter", style = primary) }
        else items(items = devices, key = { it.device.serial }) { device ->
            DeviceRow(device = device, viewModel = viewModel)
        }
    }
}

@Composable
private fun DeviceRow(device: UIDevice, viewModel: DeviceListViewModel, modifier: Modifier = Modifier) {
    val statusColor = viewModel.statusColor(device = device.device)
    val radius = with(LocalDensity.current) { 12.dp.toPx() }

    ItemRow(
        item = device,
        dividerIndentation = padding,
        modifier = modifier,
        onClick = viewModel::deviceSelected,
        onFavorite = viewModel::favorite
    ) {
        Canvas(modifier = Modifier.size(12.dp).weight(0.1f)) {
            drawCircle(color = statusColor, radius = radius)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(0.9f)) {
            Text(text = device.device.serial, style = primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = viewModel.formatDeviceAttributes(device.device),
                style = secondary,
                color = JewelTheme.contentColor
            )
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DeviceListError(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[HomeKey.DeviceListError]
    FullScreenText(primary = primary, modifier = modifier)
}

@Composable
private fun NoDevices(modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[HomeKey.EmptyDeviceList]
    FullScreenText(primary = primary, modifier = modifier)
}