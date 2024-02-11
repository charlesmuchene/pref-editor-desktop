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

package com.charlesmuchene.prefeditor.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.jetbrains.jewel.foundation.GlobalColors
import org.jetbrains.jewel.foundation.OutlineColors
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.core.theme.IntUiLightTheme
import org.jetbrains.jewel.intui.standalone.theme.dark
import org.jetbrains.jewel.intui.standalone.theme.light

val teal = Color(red = 0, green = 50, blue = 50)
val green = Color(color = 0xFF08A045)
val mustard = Color(color = 0xFFFFDB58)
val orange = Color(color = 0xFFEE7600)

@Composable
fun globalColors(isDark: Boolean): GlobalColors {
    val darkColors = GlobalColors.Companion.dark(outlines = OutlineColors.dark(focused = green))
    return if (isDark) darkColors else GlobalColors.Companion.light()
}

@Composable
fun highlightColor(): Color = if (JewelTheme.isDark) green else orange