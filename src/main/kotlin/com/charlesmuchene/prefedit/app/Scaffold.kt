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

package com.charlesmuchene.prefedit.app

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ApplicationScope
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.extensions.rememberIconPainter
import com.charlesmuchene.prefedit.providers.LocalAppState
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.ApplicationKey
import com.charlesmuchene.prefedit.resources.TextBundle
import com.charlesmuchene.prefedit.ui.padding
import com.charlesmuchene.prefedit.ui.theme.teal
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
fun ApplicationScope.scaffold(content: @Composable (Modifier) -> Unit) {
    provideAppState {
        val state = rememberWindowState(position = WindowPosition.Aligned(alignment = Alignment.Center))
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
            content(Modifier.padding(padding))
        }
    }
}

@Composable
private fun provideAppState(content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalBridge provides Bridge(),
        LocalBundle provides TextBundle(),
        LocalAppState provides AppState(),
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