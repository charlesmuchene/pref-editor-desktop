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
import com.charlesmuchene.prefedit.navigation.AppsScreen
import com.charlesmuchene.prefedit.navigation.HomeScreen
import com.charlesmuchene.prefedit.navigation.PrefEditScreen
import com.charlesmuchene.prefedit.navigation.PrefListScreen
import com.charlesmuchene.prefedit.providers.LocalNavigation
import com.charlesmuchene.prefedit.screens.app.PrefListing
import com.charlesmuchene.prefedit.screens.apps.AppsScreen
import com.charlesmuchene.prefedit.screens.home.Home
import com.charlesmuchene.prefedit.screens.preferences.PrefEditor

fun main() {
    application {
        scaffold { modifier ->
            // TODO Pills for navigation: for device -> app -> prefs
            // TODO Animate screen by sliding-left

            // TODO Theme this: see sample apps
            val screens by LocalNavigation.current.screens.collectAsState()
            when (val screen = screens.last()) {
                HomeScreen -> Home(modifier = modifier)
                is AppsScreen -> AppsScreen(modifier = modifier, device = (screen as AppsScreen).device)
                is PrefListScreen -> {
                    val prefListScreen = (screen as PrefListScreen)
                    PrefListing(modifier = modifier, device = prefListScreen.device, app = prefListScreen.app)
                }

                is PrefEditScreen -> {
                    val prefEditorScreen = (screen as PrefEditScreen)
                    PrefEditor(
                        modifier = modifier,
                        app = prefEditorScreen.app,
                        prefFile = prefEditorScreen.prefFile,
                        device = prefEditorScreen.device,
                    )
                }
            }
        }
    }
}
