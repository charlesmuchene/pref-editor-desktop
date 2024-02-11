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

package com.charlesmuchene.prefeditor.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.ui.theme.green
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.modifier.onHover
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Tooltip

@Composable
@OptIn(ExperimentalFoundationApi::class)
fun ReloadButton(modifier: Modifier = Modifier, onClick: () -> Unit) {
    val scope = rememberCoroutineScope()

    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(targetValue = if (isPressed) .9f else 1f)
    val animatedRotationAngle by remember { mutableStateOf(Animatable(initialValue = 0f)) }
    val animatedColor by animateColorAsState(targetValue = if (isHovered) green else JewelTheme.contentColor)

    Tooltip(tooltip = { Text(text = "Reload screen") }, modifier = Modifier) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = modifier
                .onHover { isHovered ->
                    scope.launch {
                        hoverAnimation(
                            isHovered = isHovered,
                            animatedRotationAngle = animatedRotationAngle
                        )
                    }
                }
                .pointerOnHover()
                .scale(animatedScale)
                .hoverable(interactionSource)
                .rotate(animatedRotationAngle.value),
        ) {
            IconButton(
                interactionSource = interactionSource,
                onClick = onClick,
                modifier = Modifier
                    .size(64.dp)
                    .padding(8.dp)
                    .clip(CircleShape)
            ) {
                val painter by rememberIconPainter(name = "reload")
                Icon(
                    contentDescription = "Reload",
                    modifier = Modifier.size(24.dp),
                    tint = animatedColor,
                    painter = painter,
                )
            }
        }
    }
}

private suspend fun hoverAnimation(
    isHovered: Boolean,
    animatedRotationAngle: Animatable<Float, AnimationVector1D>,
) {
    if (isHovered) animatedRotationAngle.animateTo(targetValue = 45f, animationSpec = tween(durationMillis = 300))
    else animatedRotationAngle.animateTo(
        targetValue = 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow,
        )
    )
}