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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.ApplicationKey.Title
import com.charlesmuchene.prefeditor.theme.EditorTheme.*
import com.charlesmuchene.prefeditor.ui.theme.teal
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.window.DecoratedWindowScope
import org.jetbrains.jewel.window.TitleBar
import org.jetbrains.jewel.window.newFullscreenControls
import java.awt.Desktop
import java.net.URI

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun DecoratedWindowScope.TitleBarView() {
    val appState = LocalAppState.current
    val title = LocalBundle.current[Title]
    TitleBar(modifier = Modifier.newFullscreenControls(), gradientStartColor = teal) {
        Text(text = title)
        Row(modifier = Modifier.align(Alignment.End)) {
            Tooltip(tooltip = {
                Text("Open Pref Editor Github repository")
            }) {
                IconButton(
                    onClick = {
                        Desktop.getDesktop().browse(URI.create("https://github.com/charlesmuchene/pref-editor-desktop"))
                    },
                    modifier = Modifier.size(40.dp).padding(5.dp)
                ) {
                    Icon(
                        resource = "icons/github@20x20.svg",
                        contentDescription = "Github Repo",
                        iconClass = AppState::class.java,
                    )
                }
            }
            Tooltip(tooltip = {
                val text = when (appState.theme) {
                    Light -> "Switch to dark theme"
                    Dark -> "Switch to system theme"
                    System -> "Switch to light theme"
                }
                Text(text = text)
            }) {
                IconButton(
                    onClick = { appState.switchTheme() },
                    modifier = Modifier.size(40.dp).padding(5.dp)
                ) {
                    when (appState.theme) {
                        Light -> Icon(
                            resource = "icons/lightTheme@20x20.svg",
                            contentDescription = "Themes",
                            iconClass = AppState::class.java,
                        )

                        Dark -> Icon(
                            resource = "icons/darkTheme@20x20.svg",
                            contentDescription = "Themes",
                            iconClass = AppState::class.java,
                        )

                        System -> Icon(
                            resource = "icons/systemTheme@20x20.svg",
                            contentDescription = "Themes",
                            iconClass = AppState::class.java,
                        )
                    }
                }
            }
        }
    }
}