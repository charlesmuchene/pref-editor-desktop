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

package com.charlesmuchene.prefeditor.screens.preferences.editor.entries.rows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.charlesmuchene.prefeditor.data.IntEntry
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.componentSpacing
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.nameComponentWeight
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.valueComponentWeight
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun IntEntryRow(entry: IntEntry, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing)
    ) {
        Text(text = entry.name, style = Typography.secondary, modifier = Modifier.weight(nameComponentWeight))
        var value by remember { mutableStateOf(entry.value) }
        var outline by remember { mutableStateOf(Outline.None) }
        TextField(
            value = value,
            outline = outline,
            onValueChange = { changed ->
                value = changed
                outline = viewModel.outline(entry, value)
                viewModel.edited(entry = entry, change = changed)
            },
            placeholder = { Text(text = entry.value) },
            modifier = Modifier.weight(valueComponentWeight),
            keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number),
        )
    }
}