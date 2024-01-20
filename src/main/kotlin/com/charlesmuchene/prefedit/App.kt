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

package com.charlesmuchene.prefedit

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.AppKey.Title
import com.charlesmuchene.prefedit.resources.TextBundle
import com.charlesmuchene.prefedit.screens.home.Home
import com.charlesmuchene.prefedit.ui.padding

fun main() = application {
    val bridge = Bridge()

    val state = rememberWindowState(position = WindowPosition.Aligned(alignment = Alignment.Center))
    val icon = rememberVectorPainter(Icons.Rounded.Edit)

    initProviders {
        val bundle = LocalBundle.current
        Window(state = state, title = bundle[Title], onCloseRequest = ::exitApplication, icon = icon) {
            Home(bridge = bridge, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun initProviders(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBridge provides Bridge(),
        LocalBundle provides TextBundle(),
    ) { content() }
}