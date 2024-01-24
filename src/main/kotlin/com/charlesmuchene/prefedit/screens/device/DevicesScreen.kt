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

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Devices
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.providers.LocalNavigation
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.screens.device.DevicesViewModel.UIState
import com.charlesmuchene.prefedit.ui.*
import com.charlesmuchene.prefedit.ui.theme.Typography
import com.charlesmuchene.prefedit.ui.theme.Typography.primary
import com.charlesmuchene.prefedit.ui.theme.Typography.secondary
import org.jetbrains.jewel.ui.component.Text

@Composable
fun DevicesScreen(modifier: Modifier = Modifier) {

    val bridge = LocalBridge.current
    val bundle = LocalBundle.current
    val navigation = LocalNavigation.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        DevicesViewModel(scope = scope, bridge = bridge, navigation = navigation, bundle = bundle)
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        UIState.Error -> DeviceListError(modifier = modifier)
        UIState.NoDevices -> NoDevices(modifier = modifier)
        is UIState.Devices -> DeviceList(
            devices = (state as UIState.Devices).devices,
            modifier = modifier,
            viewModel = viewModel
        )
    }

    // TODO Collect similar values
    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun DeviceList(devices: Devices, viewModel: DevicesViewModel, modifier: Modifier = Modifier) {
    val header = LocalBundle.current[HomeKey.ConnectedDevices]

    Listing(header = header, filterPlaceholder = "Filter devices", modifier = modifier, onFilter = viewModel::filter) {
        if (devices.isEmpty()) item { Text(text = "No devices matching filter", style = Typography.primary) }
        else items(items = devices, key = Device::serial) { device ->
            DeviceRow(device = device, viewModel = viewModel)
        }
    }
}

@Composable
private fun DeviceRow(device: Device, viewModel: DevicesViewModel, modifier: Modifier = Modifier) {
    val statusColor = viewModel.statusColor(device = device)
    val radius = with(LocalDensity.current) { 12.dp.toPx() }

    ListingRow(item = device, dividerIndentation = padding, modifier = modifier, onClick = viewModel::deviceSelected) {
        Canvas(modifier = Modifier.size(12.dp).weight(0.1f)) {
            drawCircle(color = statusColor, radius = radius)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(0.9f)) {
            Text(text = device.serial, style = primary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = viewModel.formatDeviceAttributes(device), style = secondary, color = Color.DarkGray)
            Spacer(modifier = Modifier.height(4.dp))
        }
    }
}

@Composable
private fun DeviceListError(modifier: Modifier = Modifier) {
    SingleText(key = HomeKey.DeviceListError, modifier = modifier)
}

@Composable
private fun NoDevices(modifier: Modifier = Modifier) {
    SingleText(key = HomeKey.EmptyDeviceList, modifier = modifier)
}