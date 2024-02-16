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
import com.charlesmuchene.prefeditor.ui.theme.styles.chipStyle
import com.charlesmuchene.prefeditor.ui.theme.styles.textFieldStyle
import org.jetbrains.jewel.foundation.LocalGlobalColors
import org.jetbrains.jewel.foundation.LocalGlobalMetrics
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.foundation.theme.ThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.lightThemeDefinition
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.component.styling.LocalChipStyle
import org.jetbrains.jewel.ui.component.styling.LocalTextFieldStyle

@Composable
fun ComponentStyling.prefEditorStyle(isDark: Boolean): ComponentStyling =
    decoratedWindow()
        .provide {
            arrayOf(
                LocalGlobalMetrics provides globalMetrics(),
                LocalChipStyle provides chipStyle(isDark),
                LocalGlobalColors provides globalColors(isDark),
                LocalTextFieldStyle provides textFieldStyle(isDark),
            )
        }

@Composable
fun theme(isDark: Boolean): ThemeDefinition =
    if (isDark) {
        JewelTheme.darkThemeDefinition()
    } else {
        JewelTheme.lightThemeDefinition()
    }
