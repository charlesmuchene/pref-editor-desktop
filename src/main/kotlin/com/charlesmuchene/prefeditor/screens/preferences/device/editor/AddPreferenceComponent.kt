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

package com.charlesmuchene.prefeditor.screens.preferences.device.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.charlesmuchene.prefeditor.app.AppState
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.models.PreferenceType
import com.charlesmuchene.prefeditor.ui.halfPadding
import com.charlesmuchene.prefeditor.ui.padding
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Dropdown
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.Typography

@Composable
fun AddPreferenceComponent(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAdd: (String, String, PreferenceType?) -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier =
                modifier
                    .fillMaxWidth(fraction = .6f)
                    .clip(RoundedCornerShape(percent = 5))
                    .background(JewelTheme.globalColors.paneBackground)
                    .padding(padding),
        ) {
            Text(
                text = "Add preference",
                textAlign = TextAlign.Center,
                style = Typography.h1TextStyle(),
                modifier = Modifier.fillMaxWidth(),
            )

            Spacer(modifier = Modifier.height(halfPadding))
            Divider(orientation = Orientation.Horizontal, color = Color.LightGray.copy(alpha = .5f))
            Spacer(modifier = Modifier.height(halfPadding))
            Text(text = "Name", style = Typography.h3TextStyle(), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))

            var name by remember { mutableStateOf("") }
            TextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), placeholder = {
                Text(text = "Preference Name")
            })

            Spacer(modifier = Modifier.height(halfPadding))
            Text(text = "Value", style = Typography.h3TextStyle(), modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(4.dp))

            var value by remember { mutableStateOf("") }
            TextField(value = value, onValueChange = { value = it }, modifier = Modifier.fillMaxWidth(), placeholder = {
                Text(text = "Preference Value")
            })

            Spacer(modifier = Modifier.height(padding))

            var preferenceType by remember { mutableStateOf<PreferenceType?>(null) }

            Dropdown(
                modifier = Modifier.fillMaxWidth(),
                menuContent = {
                    PreferenceType.entries.forEach { type ->
                        selectableItem(
                            selected = type == preferenceType,
                            iconResource = "icons/${type.icon}.svg",
                            onClick = { preferenceType = type },
                            iconClass = AppState::class.java,
                        ) { Text(text = type.name) }
                    }
                },
            ) { DropdownSelection(preferenceType) }

            Spacer(modifier = Modifier.height(padding))
            Divider(orientation = Orientation.Horizontal, color = Color.LightGray)
            Spacer(modifier = Modifier.height(padding))

            DialogButtons(onDismiss = onDismiss, onAdd = { onAdd(name, value, preferenceType) })
        }
    }
}

@Composable
private fun DropdownSelection(
    type: PreferenceType?,
    modifier: Modifier = Modifier,
) {
    val (iconName, text) = (type?.icon ?: "apps") to (type?.name ?: "Data Type")

    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        val painter by rememberIconPainter(name = iconName)
        Icon(painter = painter, contentDescription = text, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
private fun DialogButtons(
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
    onAdd: () -> Unit,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        OutlinedButton(
            onClick = onDismiss,
            modifier = Modifier.pointerOnHover().weight(1f),
        ) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(padding))
        DefaultButton(
            onClick = onAdd,
            modifier = Modifier.pointerOnHover().weight(1f),
        ) {
            Text(text = "Add")
        }
    }
}
