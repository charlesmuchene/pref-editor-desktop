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

import androidx.compose.ui.graphics.Color
import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Device.Type
import com.charlesmuchene.prefeditor.data.Devices
import com.charlesmuchene.prefeditor.models.UIDevice
import com.charlesmuchene.prefeditor.navigation.AppsScreen
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.resources.HomeKey
import com.charlesmuchene.prefeditor.resources.TextBundle
import com.charlesmuchene.prefeditor.ui.theme.green
import com.charlesmuchene.prefeditor.usecases.favorites.FavoritesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DevicesViewModel(
    private val bridge: Bridge,
    private val bundle: TextBundle,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
    private val listDevicesUseCase: ListDevicesUseCase = ListDevicesUseCase(bridge = bridge),
) : CoroutineScope by scope {

    private val _uiState = MutableStateFlow<UIState>(UIState.Devices(emptyList()))
    private val _message = MutableSharedFlow<String?>()
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    init {
        launch {
            _uiState.emit(determineState())
        }
    }

    private suspend fun determineState(): UIState {
        val result = listDevicesUseCase.list()
        return when {
            result.isSuccess -> result.getOrNull()?.let { devices ->
                if (devices.isEmpty()) UIState.NoDevices else UIState.Devices(mapDevices(devices))
            } ?: UIState.Error

            else -> UIState.Error
        }
    }

    private fun mapDevices(devices: Devices): List<UIDevice> = devices.map { device ->
        val isFavorite = favorites.isFavorite(device)
        UIDevice(device = device, isFavorite = isFavorite)
    }

    fun formatDeviceAttributes(device: Device): String = device.attributes.joinToString(
        separator = " ",
        transform = { attribute ->
            "${attribute.name}:${attribute.value}"
        }
    )

    fun statusColor(device: Device): Color = if (device.type == Type.Device) green
    else Color.Red

    fun deviceSelected(device: UIDevice) {
        launch {
            when (device.device.type) {
                Type.Device -> navigation.navigate(screen = AppsScreen(device = device.device))
                Type.Unknown -> _message.emit(bundle[HomeKey.UnknownDevice])
                Type.Unauthorized -> _message.emit(bundle[HomeKey.UnauthorizedDevice])
            }
        }
    }

    fun filter(input: String) {
        launch {
            _uiState.emit(UIState.Devices(mapDevices(listDevicesUseCase.devices.filter { device ->
                device.serial.contains(other = input, ignoreCase = true)
            }))) // TODO Include meta-date in filter
        }
    }

    fun favorite(device: UIDevice) {
        launch {
            if (device.isFavorite) favorites.unfavoriteDevice(device = device.device)
            else favorites.favoriteDevice(device = device.device)
            _uiState.emit(UIState.Devices(mapDevices(listDevicesUseCase.devices)))
        }
    }

    sealed interface UIState {
        data object Error : UIState
        data object NoDevices : UIState
        data class Devices(val devices: List<UIDevice>) : UIState
    }

}