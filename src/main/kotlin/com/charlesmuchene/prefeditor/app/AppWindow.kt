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

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.window.ApplicationScope
import com.charlesmuchene.prefeditor.navigation.AppsScreen
import com.charlesmuchene.prefeditor.navigation.DevicesScreen
import com.charlesmuchene.prefeditor.navigation.EditScreen
import com.charlesmuchene.prefeditor.navigation.FilesScreen
import com.charlesmuchene.prefeditor.navigation.NavigationBar
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.screens.apps.AppListScreen
import com.charlesmuchene.prefeditor.screens.device.DeviceListScreen
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferencesScreen
import com.charlesmuchene.prefeditor.screens.preffile.FileListScreen
import com.charlesmuchene.prefeditor.ui.Toast

@Composable
fun ApplicationScope.AppWindow(
    icon: Painter,
    appState: AppState,
    modifier: Modifier = Modifier,
) {
    Scaffold(icon = icon, appState = appState, modifier = modifier) {
        // TODO Animate screen by sliding-left

        val currentScreen by LocalNavigation.current.currentScreen.collectAsState()
        NavigationBar(current = currentScreen)

        val message by LocalAppState.current.toastMessage.collectAsState(initial = null)
        message?.let { text -> Toast(text = text) }

        when (val screen = currentScreen) {
            DevicesScreen -> DeviceListScreen(modifier = it)
            is AppsScreen -> AppListScreen(modifier = it, screen = screen)
            is FilesScreen -> FileListScreen(modifier = it, screen = screen)
            is EditScreen -> PreferencesScreen(modifier = it, screen = screen)
        }
    }
}
