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

package com.charlesmuchene.prefedit.screens.home

import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.bridge.BridgeStatus
import com.charlesmuchene.prefedit.bridge.BridgeStatus.Unknown
import com.charlesmuchene.prefedit.data.Device
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeViewModel(private val scope: CoroutineScope, private val dispatcher: CoroutineDispatcher = Dispatchers.IO) :
    CoroutineScope by scope + dispatcher{

    private val _bridgeStatus = MutableStateFlow<BridgeStatus>(Unknown)
    val bridgeStatus: StateFlow<BridgeStatus> = _bridgeStatus.asStateFlow()

    init {
        launch { _bridgeStatus.emit(Bridge.checkBridge()) }
    }

    fun formatDeviceAttributes(device: Device): String = device.attributes.joinToString(transform = { attribute ->
        "${attribute.name}:${attribute.value}"
    })
}