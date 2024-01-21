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

import androidx.compose.ui.graphics.Color
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.command.ListDevices
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.ui.theme.green
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BridgeAvailableViewModel(
    private val bridge: Bridge,
    private val scope: CoroutineScope,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : CoroutineScope by scope + dispatcher {

    private val _uiState = MutableStateFlow<UIState>(UIState.Devices(emptyList()))
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    init {
        launch { _uiState.emit(determineState()) }
    }

    private suspend fun determineState(): UIState {
        val result = bridge.execute(command = ListDevices())
        return when {
            result.isSuccess -> result.getOrNull()?.let { devices ->
                if (devices.isEmpty()) UIState.NoDevices
                else UIState.Devices(devices)
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    fun formatDeviceAttributes(device: Device): String = device.attributes.joinToString(
        separator = " ",
        transform = { attribute ->
            "${attribute.name}:${attribute.value}"
        }
    )

    fun statusColor(device: Device): Color = if (device.type == Device.Type.Device) green
    else Color.Red

    sealed interface UIState {
        data object Error : UIState
        data object NoDevices : UIState
        data class Devices(val devices: List<Device>) : UIState
    }

}