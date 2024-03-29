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

package com.charlesmuchene.prefeditor.navigation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.providers.LocalReloadSignal
import com.charlesmuchene.prefeditor.ui.APP_HALF_SPACING
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import com.charlesmuchene.prefeditor.ui.ReloadButton
import com.charlesmuchene.prefeditor.ui.theme.appGray
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Chip
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip
import org.jetbrains.jewel.ui.component.Typography

@Composable
@OptIn(ExperimentalLayoutApi::class)
fun NavigationBar(
    current: Screen,
    modifier: Modifier = Modifier,
) {
    val navigation = LocalNavigation.current
    val reloadSignal = LocalReloadSignal.current
    val screens by remember(current) { mutableStateOf(navigation.screens) }

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth().padding(horizontal = APP_SPACING),
        ) {
            FlowRow(
                modifier = Modifier.padding(vertical = APP_HALF_SPACING),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                screens.forEach { screen ->
                    Screen(screen = screen, selected = current == screen)
                }
            }
            ReloadButton(onClick = reloadSignal::reload)
        }
        Divider(orientation = Orientation.Horizontal, color = appGray)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun Screen(
    screen: Screen,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val navigation = LocalNavigation.current

    val (text, icon) = screenInfo(screen = screen)

    val painter by rememberIconPainter(icon)

    Tooltip(tooltip = { Text(text = "Navigate to $text") }) {
        Row(
            modifier = modifier.padding(horizontal = 2.dp).pointerOnHover(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Chip(onClick = { navigation.navigate(screen = screen) }, selected = selected) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painter,
                        contentDescription = text,
                        modifier = Modifier.size(20.dp),
                        tint = JewelTheme.contentColor,
                    )
                    Spacer(modifier = Modifier.width(APP_HALF_SPACING))
                    Text(text = text, style = Typography.labelTextStyle())
                }
            }
            if (!selected) Text(text = ">", style = Typography.h2TextStyle())
        }
    }
}

private fun screenInfo(screen: Screen): Pair<String, String> =
    when (screen) {
        DevicesScreen -> "Home" to "home"
        is AppsScreen -> screen.device.serial to "phone"
        is FilesScreen -> screen.app.packageName to "apps"
        is EditScreen -> screen.file.name to "files"
    }
