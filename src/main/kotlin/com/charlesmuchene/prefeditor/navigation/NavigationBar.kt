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

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Chip
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Typography

@Composable
fun NavigationBar(current: Screen, modifier: Modifier = Modifier) {

    val screens = LocalNavigation.current.screens

    Column(modifier = modifier.fillMaxWidth()) {
        LazyRow(
            modifier = Modifier,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(items = screens, key = { it::class.java }) { screen ->
                item(screen = screen, selected = current == screen)
            }
        }
        Spacer(modifier.height(12.dp))
        Divider(orientation = Orientation.Horizontal, color = Color.LightGray)
    }
}

@Composable
private fun item(screen: Screen, selected: Boolean, modifier: Modifier = Modifier) {
    val navigation = LocalNavigation.current

    val text = when (screen) {
        HomeScreen -> "Home"
        is AppsScreen -> screen.device.serial
        is PrefListScreen -> screen.app.packageName
        is PrefEditScreen -> screen.prefFile.name
        else -> "Unknown"
    }
    Row(
        modifier = modifier.padding(horizontal = 2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Chip(selected = selected, onClick = { navigation.navigate(screen = screen) }) { Text(text = text) }
        if (!selected) Text(text = ">", style = Typography.h2TextStyle())
    }
}