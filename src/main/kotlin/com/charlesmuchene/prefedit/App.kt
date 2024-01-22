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

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.window.application
import com.charlesmuchene.prefedit.app.scaffold
import com.charlesmuchene.prefedit.navigation.Apps
import com.charlesmuchene.prefedit.navigation.Home
import com.charlesmuchene.prefedit.navigation.PrefEdit
import com.charlesmuchene.prefedit.navigation.PrefList
import com.charlesmuchene.prefedit.providers.LocalAppState
import com.charlesmuchene.prefedit.screens.app.PrefListing
import com.charlesmuchene.prefedit.screens.device.AppsScreen
import com.charlesmuchene.prefedit.screens.home.Home
import com.charlesmuchene.prefedit.screens.prefs.PrefEditor

fun main() {
    application {
        scaffold { modifier ->
            // TODO Pills for navigation: for device -> app -> prefs
            // TODO Animate screen by sliding-left
            val screen by LocalAppState.current.screen.collectAsState()
            when (screen) {
                Home -> Home(modifier = modifier)
                is Apps -> AppsScreen(modifier = modifier, device = (screen as Apps).device)
                is PrefList -> {
                    val prefList = (screen as PrefList)
                    PrefListing(modifier = modifier, device = prefList.device, app = prefList.app)
                }

                is PrefEdit -> {
                    val prefEditor = (screen as PrefEdit)
                    PrefEditor(
                        modifier = modifier,
                        app = prefEditor.app,
                        file = prefEditor.file,
                        device = prefEditor.device,
                    )
                }
            }
        }
    }
}
