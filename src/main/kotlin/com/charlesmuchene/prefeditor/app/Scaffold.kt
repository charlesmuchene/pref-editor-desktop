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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.navigation.Navigation
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.ApplicationKey
import com.charlesmuchene.prefeditor.resources.TextBundle
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.teal
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.ThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.intui.window.styling.light
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.window.DecoratedWindow
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import org.jetbrains.jewel.window.styling.DecoratedWindowStyle
import org.jetbrains.jewel.window.styling.TitleBarStyle

@Composable
fun ApplicationScope.scaffold(content: @Composable ColumnScope.(Modifier) -> Unit) {
    provideAppState {
        val (width, height) = LocalAppState.current.windowSize
        val position = WindowPosition.Aligned(alignment = Alignment.Center)
        val state = rememberWindowState(position = position, width = width, height = height)
        val painter by rememberIconPainter(name = "app")
        val title = LocalBundle.current[ApplicationKey.Title]
        DecoratedWindow(
            state = state,
            icon = painter,
            title = title,
            onCloseRequest = ::exitApplication,
        ) {
            TitleBar(
                gradientStartColor = teal,
                modifier = Modifier.newFullscreenControls()
            ) { Text(text = title) }
            Column(
                modifier = Modifier.padding(start = padding, end = padding, top = 12.dp, bottom = padding)
            ) {
                content(Modifier.padding(top = 12.dp))
            }
        }
    }
}

@Composable
private fun provideAppState(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBridge provides Bridge(),
        LocalBundle provides TextBundle(),
        LocalAppState provides AppState(),
        LocalNavigation provides Navigation(rememberCoroutineScope()),
    ) {
        val viewModel = LocalAppState.current
        val isDark = viewModel.theme.isDark()

        IntUiTheme(
            content = content,
            theme = theme(isDark = isDark),
            styling = ComponentStyling.decoratedWindow(
                windowStyle = windowStyle(isDark = isDark),
                titleBarStyle = titleBarStyle(isDark = isDark),
            ),
        )
    }
}

@Composable
private fun theme(isDark: Boolean): ThemeDefinition =
    if (isDark) JewelTheme.darkThemeDefinition() else JewelTheme.lightThemeDefinition()

@Composable
private fun windowStyle(isDark: Boolean): DecoratedWindowStyle =
    if (isDark) DecoratedWindowStyle.dark() else DecoratedWindowStyle.light()

@Composable
private fun titleBarStyle(isDark: Boolean): TitleBarStyle = if (isDark) TitleBarStyle.dark() else TitleBarStyle.light()