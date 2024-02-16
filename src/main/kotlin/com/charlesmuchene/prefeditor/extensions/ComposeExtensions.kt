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

package com.charlesmuchene.prefeditor.extensions

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import com.charlesmuchene.prefeditor.models.IconExtension
import com.charlesmuchene.prefeditor.models.IconExtension.SVG
import com.charlesmuchene.prefeditor.resources.TextBundle
import org.jetbrains.jewel.ui.painter.rememberResourcePainterProvider
import java.awt.Cursor

@Composable
fun rememberIconPainter(name: String, extension: IconExtension = SVG): State<Painter> =
    rememberResourcePainterProvider(
        iconClass = TextBundle::class.java,
        path = "icons/$name.${extension.extension}",
    ).getPainter()

@Composable
fun Modifier.pointerOnHover() = pointerHoverIcon(PointerIcon(Cursor(Cursor.HAND_CURSOR)))

fun screenTransitionSpec(): ContentTransform =
    (fadeIn(animationSpec = tween(durationMillis = 220, delayMillis = 90)) +
            scaleIn(initialScale = 0.92f, animationSpec = tween(durationMillis = 220, delayMillis = 90)))
        .togetherWith(
            fadeOut(animationSpec = tween(durationMillis = 300)) +
                    scaleOut(targetScale = .92f, animationSpec = tween(durationMillis = 220))
        )