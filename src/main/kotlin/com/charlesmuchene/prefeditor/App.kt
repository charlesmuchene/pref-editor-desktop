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

@file:JvmName(name = "App")

package com.charlesmuchene.prefeditor

import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.charlesmuchene.prefeditor.app.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val icon = svgResource(resource = "app")

    application {
        var isSettingUp by remember { mutableStateOf(value = true) }
        var appState by remember { mutableStateOf<AppState?>(value = null) }

        LaunchedEffect(Unit) {
            joinAll(launch { appState = appSetup() }, launch { delay(timeMillis = 1_500) })
            isSettingUp = false
        }

        if (isSettingUp) SetupWindow()
        else appState?.let { AppWindow(icon = icon, appState = it) } ?: error("Unusable. No AppState found!")
    }
}
