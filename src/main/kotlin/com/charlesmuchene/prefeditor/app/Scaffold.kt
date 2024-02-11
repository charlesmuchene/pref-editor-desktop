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
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.ResourceLoader
import androidx.compose.ui.res.loadSvgPainter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.models.ReloadSignal
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.providers.*
import com.charlesmuchene.prefeditor.resources.ApplicationKey
import com.charlesmuchene.prefeditor.resources.TextBundle
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.prefEditorStyle
import com.charlesmuchene.prefeditor.ui.theme.theme
import org.jetbrains.jewel.foundation.modifier.trackActivation
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.window.DecoratedWindow

@Composable
fun ApplicationScope.scaffold(icon: Painter, appState: AppState, content: @Composable ColumnScope.(Modifier) -> Unit) {
    provideAppState(appState = appState) {
        val (width, height) = LocalAppState.current.windowSize
        val position = WindowPosition.Aligned(alignment = Alignment.Center)
        val state = rememberWindowState(position = position, width = width, height = height)
        val title = LocalBundle.current[ApplicationKey.Title]
        DecoratedWindow(
            state = state,
            icon = icon,
            title = title,
            onCloseRequest = ::exitApplication,
        ) {
            TitleBarView()
            Column(
                modifier = Modifier
                    .background(JewelTheme.globalColors.paneBackground)
            ) {
                content(
                    Modifier
                        .trackActivation()
                        .padding(horizontal = padding, vertical = padding * 0.5f)
                )
            }
        }
    }
}

@Composable
fun provideAppState(appState: AppState, content: @Composable () -> Unit) {
    val scope = rememberCoroutineScope()
    CompositionLocalProvider(
        LocalAppState provides appState,
        LocalBridge provides Bridge(),
        LocalBundle provides TextBundle(),
        LocalNavigation provides Navigation(scope),
        LocalReloadSignal provides ReloadSignal(scope),
    ) {
        val state = LocalAppState.current
        val isDark = state.theme.isDark()

        IntUiTheme(
            content = content,
            theme = theme(isDark = isDark),
            styling = ComponentStyling.prefEditorStyle(isDark),
        )
    }
}

@OptIn(ExperimentalComposeUiApi::class)
fun svgResource(
    resource: String,
    loader: ResourceLoader = ResourceLoader.Default,
): Painter = loader.load(resourcePath = "icons/$resource.svg").use { stream ->
    loadSvgPainter(inputStream = stream, density = Density(density = 1f))
}