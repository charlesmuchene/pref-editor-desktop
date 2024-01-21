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

package com.charlesmuchene.prefedit.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType

object PrefEditFonts {
    val heading = TextStyle.Default.copy(fontSize = TextUnit(value = 24f, type = TextUnitType.Sp))
    val primary = TextStyle.Default.copy(fontSize = TextUnit(value = 16f, type = TextUnitType.Sp))
    val secondary = TextStyle.Default.copy(fontSize = TextUnit(value = 12f, type = TextUnitType.Sp), color = Color.LightGray)
}