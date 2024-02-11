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

package com.charlesmuchene.prefeditor.ui.filter

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.ui.theme.green
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun FilterTextField(
    placeholder: String,
    modifier: Modifier = Modifier,
    filtered: (String) -> Unit,
    onClear: () -> Unit,
) {
    var value by remember { mutableStateOf("") }
    val filterPainter by rememberIconPainter(name = "filter")
    val clearPainter by rememberIconPainter(name = "clear")
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    TextField(
        leadingIcon = {
            Icon(
                painter = filterPainter,
                contentDescription = "Filter",
                tint = if (isHovered) green else Color.LightGray,
                modifier = Modifier.size(20.dp).padding(4.dp),
            )
        },
        trailingIcon = {
            IconButton(onClick = {
                value = ""
                onClear()
            }) {
                Icon(
                    painter = clearPainter,
                    contentDescription = "Clear",
                    tint = if (isHovered) green else Color.LightGray,
                    modifier = Modifier.pointerOnHover().size(20.dp).padding(4.dp),
                )
            }
        },
        value = value,
        placeholder = { Text(text = placeholder) },
        modifier = modifier.hoverable(interactionSource),
        onValueChange = { text ->
            value = text
            filtered(text)
        }
    )
}