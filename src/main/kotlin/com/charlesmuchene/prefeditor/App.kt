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
import com.charlesmuchene.prefeditor.models.AppStatus
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.delay

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val icon = svgResource(resource = "app")

    application {
        var status by remember { mutableStateOf<AppStatus>(AppStatus.Initializing) }

        LaunchedEffect(Unit) {
            status = awaitAll(async { appSetup() }, async { delay(timeMillis = 1_000) }).first() as AppStatus
        }

        when (status) {
            AppStatus.Initializing -> SetupWindow()
            is AppStatus.NoBridge -> NoBridgeWindow(status = status as AppStatus.NoBridge, icon = icon)
            is AppStatus.Ready -> AppWindow(icon = icon, appState = (status as AppStatus.Ready).state)
        }
    }
}
