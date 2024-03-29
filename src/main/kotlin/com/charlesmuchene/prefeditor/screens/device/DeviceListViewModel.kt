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
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Device.Type
import com.charlesmuchene.prefeditor.data.Devices
import com.charlesmuchene.prefeditor.extensions.throttleLatest
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.models.UIDevice
import com.charlesmuchene.prefeditor.navigation.AppsScreen
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.resources.DevicesKey
import com.charlesmuchene.prefeditor.resources.TextBundle
import com.charlesmuchene.prefeditor.screens.device.DeviceListUseCase.FetchStatus
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.ui.theme.green
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class DeviceListViewModel(
    executable: String,
    reloadSignal: ReloadSignal,
    private val bundle: TextBundle,
    private val scope: CoroutineScope,
    private val navigation: Navigation,
    private val favorites: FavoritesUseCase,
) : CoroutineScope by scope {
    private val processor = Processor()
    private val decoder = DeviceListDecoder()
    val command = DeviceListCommand(executable)
    private val useCase = DeviceListUseCase(processor = processor, decoder = decoder, command = command)

    private var filter = ItemFilter.none

    private val _uiState = MutableStateFlow<UIState>(UIState.Loading)
    val uiState: StateFlow<UIState> = _uiState.asStateFlow()

    private val _message = MutableSharedFlow<String?>()
    val message: SharedFlow<String?> = _message.asSharedFlow()

    private val _filtered = MutableSharedFlow<List<UIDevice>>()
    val filtered: SharedFlow<List<UIDevice>> = _filtered.asSharedFlow()

    init {
        useCase.status
            .onEach { _uiState.emit(mapToState(it)) }
            .launchIn(scope = scope)

        reloadSignal.signal
            .throttleLatest(delayMillis = 300)
            .onEach { useCase.fetch() }
            .drop(count = 1)
            .onEach { _message.emit("Devices reloaded") }
            .launchIn(scope = scope)
    }

    /**
     * Map use case status to a UI state
     *
     * @param status A [FetchStatus]
     * @return An instance of [UIState]
     */
    private fun mapToState(status: FetchStatus): UIState =
        when (status) {
            FetchStatus.Fetching -> UIState.Loading
            is FetchStatus.Done -> {
                val devices = status.devices
                if (devices.isEmpty()) {
                    UIState.NoDevices
                } else {
                    UIState.Devices(filter(filter = filter, devices = mapDevices(devices)))
                }
            }

            is FetchStatus.Error -> UIState.Error(status.message)
        }

    /**
     * Map connected devices to a UI model.
     *
     * @param devices A [List] of connected [Device]s
     * @return A [List] of [UIDevice]
     * @see [UIDevice]
     */
    private fun mapDevices(devices: Devices): List<UIDevice> =
        devices.map { device ->
            val isFavorite = favorites.isFavorite(device)
            UIDevice(device = device, isFavorite = isFavorite)
        }

    /**
     * Format device attributes
     *
     * @param device [Device] to get attributes from
     * @return A formatted string
     */
    fun formatDeviceAttributes(device: Device): String =
        device.attributes.joinToString(
            separator = " ",
            transform = { attribute ->
                "${attribute.name}:${attribute.value}"
            },
        )

    /**
     * Status color of displayed device
     *
     * @param device [Device] instance
     * @return [Color] for based on device state
     */
    fun statusColor(device: Device): Color =
        if (device.type == Type.Device) {
            green
        } else {
            Color.Red
        }

    /**
     * Select a device
     *
     * @param device Selected [UIDevice]
     */
    fun select(device: UIDevice) {
        launch {
            when (device.device.type) {
                Type.Device -> navigation.navigate(screen = AppsScreen(device = device.device))
                Type.Unknown -> _message.emit(bundle[DevicesKey.UnknownDevice])
                Type.Unauthorized -> _message.emit(bundle[DevicesKey.UnauthorizedDevice])
            }
        }
    }

    /**
     * Filter content based on input
     *
     * Invoking this function with a value clears the filter
     * @param filter [ItemFilter]
     */
    fun filter(filter: ItemFilter) {
        this.filter = filter
        launch {
            val status = useCase.status.value
            if (status is FetchStatus.Done) {
                val devices = mapDevices(status.devices)
                _filtered.emit(filter(filter = filter, devices = devices))
            }
        }
    }

    /**
     * Filter given list of devices
     *
     * @param filter [ItemFilter] to apply
     * @param devices The [List] of [UIDevice]s to filter
     * @return The filtered [List] of [UIDevice]s
     */
    private fun filter(
        filter: ItemFilter,
        devices: List<UIDevice>,
    ): ImmutableList<UIDevice> =
        devices.filter { uiDevice ->
            (if (filter.starred) uiDevice.isFavorite else true) &&
                (
                    uiDevice.device.serial.contains(other = filter.text, ignoreCase = true) ||
                        uiDevice.device.attributes.joinToString()
                            .contains(other = filter.text, ignoreCase = true)
                )
        }.toImmutableList()

    /**
     * Un/Favorite a device
     *
     * @param device [UIDevice] to un/favorite
     */
    suspend fun favorite(device: UIDevice) =
        coroutineScope {
            async {
                if (device.isFavorite) {
                    favorites.unfavoriteDevice(device = device.device)
                } else {
                    favorites.favoriteDevice(device = device.device)
                }
                device.copy(isFavorite = !device.isFavorite)
            }.await()
        }

    sealed interface UIState {
        data class Error(val message: String?) : UIState

        data object Loading : UIState

        data object NoDevices : UIState

        data class Devices(val devices: ImmutableList<UIDevice>) : UIState
    }
}
