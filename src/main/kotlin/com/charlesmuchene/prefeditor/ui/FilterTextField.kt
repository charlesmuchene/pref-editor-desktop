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

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import org.jetbrains.jewel.foundation.modifier.onHover
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun FilterTextField(placeHolder: String, modifier: Modifier = Modifier, changed: (String) -> Unit) {
    // TODO Leading icon is search
    // TODO Trailing icon is cancel

    var value by remember { mutableStateOf("") }
    var hovered by remember { mutableStateOf(false) }
    TextField(
        value = value,
        undecorated = !hovered && value.isBlank(),
        placeholder = { Text(text = placeHolder) },
        modifier = modifier.onHover { hovered = it },
        onValueChange = { text ->
            value = text
            changed(text)
        }
    )
}