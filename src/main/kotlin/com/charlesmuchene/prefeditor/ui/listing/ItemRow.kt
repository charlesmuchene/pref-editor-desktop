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

package com.charlesmuchene.prefeditor.ui.listing

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.models.Favoritable
import com.charlesmuchene.prefeditor.ui.BreathingContainer
import com.charlesmuchene.prefeditor.ui.FavoriteButton
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.green
import com.charlesmuchene.prefeditor.ui.theme.highlightColor
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.modifier.onHover
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.LocalContentColor
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun <T : Favoritable> ItemRow(
    item: T,
    onClick: (T) -> Unit,
    onFavorite: (T) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val animatedScalePercent by remember { mutableStateOf(Animatable(initialValue = 1f)) }

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val color by animateColorAsState(
        animationSpec = tween(durationMillis = 300),
        targetValue = if (isHovered) highlightColor() else JewelTheme.contentColor,
    )

    CompositionLocalProvider(LocalContentColor provides color) {
        Column(modifier = modifier
            .fillMaxWidth()
            .clickable { onClick(item) }
            .hoverable(interactionSource)
            .pointerOnHover()
            .onHover { isHovered ->
                scope.launch {
                    hoverAnimation(isHovered = isHovered, animatedScalePercent = animatedScalePercent)
                }
            }
        ) {
            Row(
                modifier = Modifier
                    .padding(vertical = padding * .5f)
                    .scale(animatedScalePercent.value),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(.9f),
                    content = content,
                )
                Spacer(modifier = Modifier.width(24.dp))
                BreathingContainer {
                    FavoriteButton(
                        selected = item.isFavorite,
                        modifier = Modifier.weight(.1f),
                        onFavorite = { onFavorite(item) },
                    )
                }
            }
            Divider(
                orientation = Orientation.Horizontal,
                color = Color.LightGray.copy(alpha = 0.5f),
            )
        }
    }
}

private suspend fun hoverAnimation(
    isHovered: Boolean,
    animatedScalePercent: Animatable<Float, AnimationVector1D>,
) {
    if (isHovered) animatedScalePercent.animateTo(targetValue = 1.02f, animationSpec = tween(durationMillis = 300))
    else animatedScalePercent.animateTo(targetValue = 1f, animationSpec = tween(durationMillis = 700))
}
