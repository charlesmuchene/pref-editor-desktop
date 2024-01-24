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
import com.charlesmuchene.prefedit.navigation.*
import com.charlesmuchene.prefedit.providers.LocalNavigation
import com.charlesmuchene.prefedit.screens.app.PrefListing
import com.charlesmuchene.prefedit.screens.apps.AppsScreen
import com.charlesmuchene.prefedit.screens.home.Home
import com.charlesmuchene.prefedit.screens.preferences.PrefEditor

fun main() {
    application {
        scaffold { modifier ->
            // TODO Theme this: see sample apps
            // TODO Animate screen by sliding-left

            val screen by LocalNavigation.current.current.collectAsState()
            NavigationBar(screen)

            when (screen) {
                HomeScreen -> Home(modifier = modifier)
                is AppsScreen -> AppsScreen(modifier = modifier, device = (screen as AppsScreen).device)
                is PrefListScreen -> PrefListing(modifier = modifier, device = (screen as PrefListScreen).device, app = (screen as PrefListScreen).app)
                is PrefEditScreen -> PrefEditor(
                    app = (screen as PrefEditScreen).app,
                    modifier = modifier,
                    device = (screen as PrefEditScreen).device,
                    prefFile = (screen as PrefEditScreen).prefFile,
                )
            }
        }
    }
}
