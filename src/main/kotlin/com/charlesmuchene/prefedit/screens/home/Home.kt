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

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.bridge.BridgeStatus.*
import com.charlesmuchene.prefedit.screens.home.bridge.BridgeAvailable
import com.charlesmuchene.prefedit.screens.home.bridge.BridgeStatusLoading
import com.charlesmuchene.prefedit.screens.home.bridge.BridgeUnavailable

@Preview
@Composable
fun Home(viewModel: HomeViewModel, modifier: Modifier = Modifier) {
    val bridgeStatus by viewModel.bridgeStatus.collectAsState()

    when (bridgeStatus) {
        Available -> BridgeAvailable(modifier = modifier)
        Unknown -> BridgeStatusLoading(modifier = modifier)
        Unavailable -> BridgeUnavailable(modifier = modifier)
    }
}