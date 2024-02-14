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

package com.charlesmuchene.prefeditor.screens.preferences.device.viewer

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import com.charlesmuchene.prefeditor.ui.listing.ItemListing
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.Text

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun Viewer(prefUseCase: DevicePreferencesUseCase, modifier: Modifier = Modifier, onEditClick: () -> Unit) {

    val scope = rememberCoroutineScope()
    val viewModel by remember {
        mutableStateOf(ViewerViewModel(prefUseCase = prefUseCase, scope = scope))
    }

    val items by viewModel.preferences

    Column(modifier = modifier) {
        ViewerHeader(onClick = onEditClick)
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            color = Color.LightGray.copy(alpha = 0.75f),
            orientation = Orientation.Horizontal,
        )
        ItemListing {
            items(items = items, key = Preference::name) { preference ->
                PreferenceRow(preference = preference, modifier = Modifier.animateItemPlacement())
            }
        }
    }
}

@Composable
private fun PreferenceRow(preference: Preference, modifier: Modifier = Modifier) {
    val name = preferenceIconName(preference)
    val painter by rememberIconPainter(name)

    Column(modifier = modifier.fillMaxWidth().padding(top = 8.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painter, modifier = Modifier.size(24.dp), contentDescription = name)
            Spacer(modifier = Modifier.width(padding * .5f))
            Column {
                Text(text = preference.name, fontSize = TextUnit(value = 16f, type = TextUnitType.Sp))
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = preference.value,
                    color = Color.Gray,
                    fontSize = TextUnit(value = 14f, type = TextUnitType.Sp),
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Divider(
            orientation = Orientation.Horizontal,
            color = Color.LightGray.copy(alpha = 0.5f),
        )
    }
}

@Composable
private fun ViewerHeader(modifier: Modifier = Modifier, onClick: () -> Unit) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Preferences View", style = Typography.heading, modifier = modifier)
        Box(modifier = Modifier, contentAlignment = Alignment.Center) {
            DefaultButton(onClick = onClick, modifier = Modifier.pointerOnHover()) {
                Text(text = "Edit")
            }
        }
    }
}