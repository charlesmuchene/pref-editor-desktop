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
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun NavigationBar(current: Screen, modifier: Modifier = Modifier) {

    val navigation = LocalNavigation.current
    val screens by remember(current) { mutableStateOf(navigation.screens) }

    Column(modifier = modifier.fillMaxWidth()) {
        FlowRow(
            modifier = Modifier,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            screens.forEach { screen ->
                page(screen = screen, selected = current == screen)
            }
        }
        Spacer(modifier.height(12.dp))
        Divider(orientation = Orientation.Horizontal, color = Color.LightGray)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun page(screen: Screen, selected: Boolean, modifier: Modifier = Modifier) {
    val navigation = LocalNavigation.current

    val text = when (screen) {
        HomeScreen -> "Home"
        is AppsScreen -> screen.device.serial
        is PrefListScreen -> screen.app.packageName
        is PrefEditScreen -> screen.prefFile.name
        else -> "Unknown"
    }
    Tooltip(tooltip = { Text(text = "Navigate to $text") }) {
        Row(
            modifier = modifier.padding(horizontal = 2.dp).pointerOnHover(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RadioButtonChip(
                selected = selected,
                onClick = { navigation.navigate(screen = screen) }) { Text(text = text) }
            if (!selected) Text(text = ">", style = Typography.h2TextStyle())
        }
    }
}