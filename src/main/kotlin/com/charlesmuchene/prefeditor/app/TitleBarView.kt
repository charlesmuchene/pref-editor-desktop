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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.AppKey.Title
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme.Dark
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme.Light
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme.System
import com.charlesmuchene.prefeditor.ui.APP_HALF_SPACING
import com.charlesmuchene.prefeditor.ui.APP_ICON_BUTTON_SIZE
import com.charlesmuchene.prefeditor.ui.theme.teal
import org.jetbrains.jewel.ui.component.Dropdown
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
fun DecoratedWindowScope.TitleBarView(modifier: Modifier = Modifier) {
    val appState = LocalAppState.current
    val title = LocalBundle.current[Title]
    val isDark by remember(appState.theme) { mutableStateOf(appState.theme.isDark()) }
    TitleBar(modifier = modifier.newFullscreenControls(), gradientStartColor = teal) {
        Text(text = title)
        Row(
            modifier = Modifier.align(Alignment.End),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(APP_HALF_SPACING),
        ) {
            Tooltip(tooltip = { Text(text = "View app repository") }) {
                val interactionSource = remember { MutableInteractionSource() }
                val isHovered by interactionSource.collectIsHoveredAsState()
                val animatedScale by animateFloatAsState(targetValue = if (isHovered) 1.1f else 1f)
                IconButton(
                    onClick = {
                        Desktop.getDesktop().browse(URI.create("https://github.com/charlesmuchene/pref-editor-desktop"))
                    },
                    modifier =
                        Modifier.size(APP_ICON_BUTTON_SIZE)
                            .hoverable(interactionSource)
                            .scale(animatedScale),
                ) {
                    TitleBarIcon(
                        isDark = isDark,
                        isForBar = true,
                        resource = "github",
                        contentDescription = "App's repository on github",
                    )
                }
            }
            val interactionSource = remember { MutableInteractionSource() }
            val isHovered by interactionSource.collectIsHoveredAsState()
            val animatedScale by animateFloatAsState(targetValue = if (isHovered) 1.1f else 1f)
            Dropdown(
                menuContent = {
                    EditorTheme.entries.forEach { theme ->
                        val isSelected = appState.theme == theme
                        selectableItem(selected = isSelected, onClick = { appState.changeTheme(theme) }) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                theme.Icon(isDark = isDark, isSelected = isSelected)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = "${theme.name} Theme")
                            }
                        }
                    }
                },
                modifier = Modifier.hoverable(interactionSource).scale(animatedScale),
            ) {
                Tooltip(tooltip = { Text(text = "Change theme") }) {
                    appState.theme.Icon(isDark = isDark, isForBar = true)
                }
            }
        }
    }
}

@Composable
private fun EditorTheme.Icon(
    isDark: Boolean,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    isForBar: Boolean = false,
) {
    when (this) {
        Dark ->
            TitleBarIcon(
                contentDescription = "Dark Theme",
                isSelected = isSelected,
                resource = "dark-theme",
                modifier = modifier,
                isForBar = isForBar,
                isDark = isDark,
            )

        Light ->
            TitleBarIcon(
                contentDescription = "Light Theme",
                isSelected = isSelected,
                resource = "light-theme",
                modifier = modifier,
                isForBar = isForBar,
                isDark = isDark,
            )

        System ->
            TitleBarIcon(
                contentDescription = "System Theme",
                resource = "system-theme",
                isSelected = isSelected,
                modifier = modifier,
                isForBar = isForBar,
                isDark = isDark,
            )
    }
}

@Composable
private fun TitleBarIcon(
    isDark: Boolean,
    resource: String,
    contentDescription: String,
    isForBar: Boolean = false,
    isSelected: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val selected = if (isSelected) "-selected" else ""
    val bar = if (isForBar) "@20x20" else ""
    val dark = if (isDark) "_dark" else ""
    val filename = "icons/$resource$bar$selected$dark.svg"

    Icon(
        contentDescription = contentDescription,
        modifier = modifier.pointerOnHover(),
        iconClass = AppState::class.java,
        resource = filename,
    )
}
