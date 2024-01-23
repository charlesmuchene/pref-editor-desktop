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

package com.charlesmuchene.prefedit.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.extensions.pointerOnHover
import com.charlesmuchene.prefedit.ui.theme.green
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun <T> ListingRow(
    item: T,
    onClick: (T) -> Unit = {},
    dividerIndentation: Dp = Dp.Hairline,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            content = content,
            modifier = Modifier
                .fillMaxWidth()
                .pointerOnHover()
                .hoverable(interactionSource)
                .drawBehind {
                    if (isHovered)
                        drawRoundRect(green, cornerRadius = CornerRadius(10.dp.toPx()), style = Stroke(width = 2f))
                }
                .padding(vertical = 12.dp)
                .clickable { onClick(item) },
        )
        Divider(
            orientation = Orientation.Horizontal,
            startIndent = dividerIndentation,
            color = Color.LightGray.copy(alpha = 0.5f),
        )
    }
}