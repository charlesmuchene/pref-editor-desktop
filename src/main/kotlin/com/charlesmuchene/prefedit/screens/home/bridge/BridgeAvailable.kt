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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Devices
import com.charlesmuchene.prefedit.providers.LocalAppState
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.screens.home.bridge.BridgeAvailableViewModel.UIState
import com.charlesmuchene.prefedit.ui.Listing
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.Toast
import com.charlesmuchene.prefedit.ui.padding
import com.charlesmuchene.prefedit.ui.theme.PrefEditTextStyle.primary
import com.charlesmuchene.prefedit.ui.theme.PrefEditTextStyle.secondary
import com.charlesmuchene.prefedit.ui.theme.green
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import java.awt.Cursor

@Composable
fun BridgeAvailable(modifier: Modifier = Modifier) {

    val bridge = LocalBridge.current
    val bundle = LocalBundle.current
    val appState = LocalAppState.current
    val scope = rememberCoroutineScope()
    val viewModel =
        remember { BridgeAvailableViewModel(scope = scope, bridge = bridge, appState = appState, bundle = bundle) }
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
private fun DeviceList(devices: Devices, viewModel: BridgeAvailableViewModel, modifier: Modifier = Modifier) {
    val header = LocalBundle.current[HomeKey.ConnectedDevices]

    Listing(items = devices, header = header, modifier = modifier) {
        items(items = devices, key = Device::serial) { device ->
            DeviceRow(device = device, viewModel = viewModel)
        }
    }
}

@Composable
private fun DeviceRow(device: Device, viewModel: BridgeAvailableViewModel, modifier: Modifier = Modifier) {
    val statusColor = viewModel.statusColor(device = device)
    val radius = with(LocalDensity.current) { 12.dp.toPx() }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .pointerOnHover()
                .hoverable(interactionSource)
                .drawBehind {
                    if (isHovered)
                        drawRoundRect(green, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(width = 2f))
                }
                .padding(vertical = 12.dp)
                .clickable { viewModel.deviceSelected(device = device) }
        ) {
            Canvas(modifier = Modifier.size(12.dp).weight(0.1f)) {
                drawCircle(color = statusColor, radius = radius)
            }
            Spacer(modifier = Modifier.width(4.dp))
            Column(modifier = Modifier.weight(0.85f)) {
                Text(text = device.serial, style = primary)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = viewModel.formatDeviceAttributes(device), style = secondary, color = Color.DarkGray)
                Spacer(modifier = Modifier.height(4.dp))
            }
        }
        Divider(orientation = Orientation.Horizontal, color = Color.LightGray.copy(alpha = 0.5f), startIndent = padding)
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

@Composable
private fun Modifier.pointerOnHover() = pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))