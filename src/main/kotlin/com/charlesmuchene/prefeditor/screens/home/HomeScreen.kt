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

package com.charlesmuchene.prefeditor.screens.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.bridge.BridgeStatus.*
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.screens.bridge.BridgeLoading
import com.charlesmuchene.prefeditor.screens.bridge.BridgeUnavailable
import com.charlesmuchene.prefeditor.screens.device.DeviceListScreen

@Preview
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val viewModel = remember { HomeViewModel(scope = scope, bridge = bridge) }
    val bridgeStatus by viewModel.bridgeStatus.collectAsState()

    updateTransition(bridgeStatus).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { status ->
        when (status) {
            Available -> DeviceListScreen(modifier = modifier)
            Unknown -> BridgeLoading(modifier = modifier)
            Unavailable -> BridgeUnavailable(modifier = modifier)
        }
    }
}