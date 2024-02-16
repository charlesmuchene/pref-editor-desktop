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

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.charlesmuchene.prefeditor.ui.theme.Typography
import kotlinx.coroutines.delay
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Toast(
    text: String,
    modifier: Modifier = Modifier,
) {
    var shown by remember { mutableStateOf(true) }

    LaunchedEffect(text) {
        val timeMillis = 2_000L
        delay(timeMillis = timeMillis)
        shown = false
    }

    Popup(alignment = Alignment.BottomCenter) {
        AnimatedVisibility(shown) {
            Box(
                modifier =
                    modifier
                        .height(72.dp)
                        .padding(bottom = padding)
                        .clip(RoundedCornerShape(size = 12.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .fillMaxWidth(fraction = 0.7f),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = text,
                    style = Typography.primary,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
