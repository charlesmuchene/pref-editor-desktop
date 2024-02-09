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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import org.jetbrains.jewel.ui.component.CheckboxRow

@Composable
fun FilterRow(placeholder: String, onFilter: (String) -> Unit, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilterTextField(placeholder = placeholder, filtered = onFilter)
        Spacer(modifier = Modifier.width(padding * 0.5f))
        var checked by remember { mutableStateOf(false) }
        CheckboxRow(
            modifier = Modifier.pointerOnHover(),
            checked = checked,
            onCheckedChange = { checked = !checked },
            text = "Starred"
        )
    }
}