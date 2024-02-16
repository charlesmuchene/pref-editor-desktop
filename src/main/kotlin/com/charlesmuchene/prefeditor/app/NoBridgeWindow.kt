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

package com.charlesmuchene.prefeditor.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefeditor.bridge.BridgeStatus
import com.charlesmuchene.prefeditor.models.AppStatus
import com.charlesmuchene.prefeditor.screens.bridge.BridgeUnavailable
import com.charlesmuchene.prefeditor.ui.FullScreenText
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.window.DecoratedWindow

@Composable
fun ApplicationScope.NoBridgeWindow(
    status: AppStatus.NoBridge,
    icon: Painter,
) {
    val bridgeStatus = status.bridgeStatus
    provideAppState(appState = status.state) {
        val windowState = rememberWindowState(position = WindowPosition.Aligned(Alignment.Center))
        DecoratedWindow(icon = icon, onCloseRequest = ::exitApplication, resizable = false, state = windowState) {
            TitleBarView()
            Column(modifier = Modifier.background(JewelTheme.globalColors.paneBackground)) {
                when (bridgeStatus) {
                    BridgeStatus.Available -> BridgeAvailable()
                    BridgeStatus.Unavailable -> BridgeUnavailable()
                }
            }
        }
    }
}

@Composable
private fun BridgeAvailable(modifier: Modifier = Modifier) {
    FullScreenText(primary = "Invalid UI: you shouldn't be reading this text!!!", modifier = modifier)
}
