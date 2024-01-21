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

package com.charlesmuchene.prefedit.screens.home.bridge

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.command.ListDevices
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Devices
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.screens.home.HomeViewModel
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.padding
import org.jetbrains.jewel.ui.component.Text

@Composable
fun AvailableBridge(modifier: Modifier = Modifier) {

    val bridge = LocalBridge.current
    var result by remember(bridge) {
        mutableStateOf<Result<Devices>>(Result.success(emptyList()))
    }

    LaunchedEffect(Unit) {
        result = bridge.execute(command = ListDevices())
    }

    when {
        result.isSuccess -> result.getOrNull()?.let { devices ->
            if (devices.isEmpty()) NoDevices(modifier = modifier)
            else DeviceList(devices = devices, modifier = modifier)
        } ?: DeviceListError(modifier = modifier)

        result.isFailure -> DeviceListError(modifier = modifier)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DeviceList(devices: Devices, modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current
    val header = bundle[HomeKey.ConnectedDevices]
    val viewModel = HomeViewModel()
    LazyColumn(modifier = modifier.fillMaxSize().padding(vertical = padding)) {
        stickyHeader {
            Text(text = header)
            Spacer(modifier = Modifier.height(padding))
        }
        items(items = devices, key = Device::serial) { device ->
            DeviceRow(device = device, viewModel = viewModel)
        }
    }
}

@Composable
private fun DeviceRow(device: Device, viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val color = if (device.type == Device.Type.Device) Color.Green
    else Color.LightGray
    val radius = with(LocalDensity.current) { 12.dp.toPx() }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Canvas(modifier = Modifier.size(12.dp).weight(0.1f)) {
            drawCircle(color = color, radius = radius)
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = modifier) {
            Text(text = device.serial)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = viewModel.formatAttributes(device))
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