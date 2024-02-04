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

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.application
import com.charlesmuchene.prefeditor.app.AppWindow
import com.charlesmuchene.prefeditor.app.scaffold
import com.charlesmuchene.prefeditor.app.svgResource
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.navigation.*
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.screens.apps.AppsScreen
import com.charlesmuchene.prefeditor.screens.home.Home
import com.charlesmuchene.prefeditor.screens.listing.PrefListing
import com.charlesmuchene.prefeditor.screens.preferences.PrefEditor
import com.charlesmuchene.prefeditor.ui.Toast

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val icon = svgResource(resourcePath = "icons/app.svg")

    application {
        LaunchedEffect(Unit) {
            EditorFiles.initialize()
        }

        AppWindow(icon)
    }
}
