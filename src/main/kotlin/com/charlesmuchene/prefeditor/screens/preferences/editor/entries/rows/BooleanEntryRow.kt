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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.BooleanEntry
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.componentSpacing
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.nameComponentWeight
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.valueComponentWeight
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.RadioButtonRow
import org.jetbrains.jewel.ui.component.Text

@Composable
fun BooleanEntryRow(entry: BooleanEntry, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing)
    ) {
        Text(text = entry.name, style = Typography.secondary, modifier = Modifier.weight(nameComponentWeight))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(valueComponentWeight)) {
            var isTrue by remember { mutableStateOf(entry.value) }
            val trueString = true.toString()
            val falseString = false.toString()
            RadioButtonRow(
                selected = isTrue.toBooleanStrict(),
                onClick = {
                    isTrue = trueString
                    viewModel.edited(entry = entry, change = trueString)
                },
                outline = viewModel.outline(entry = entry, value = trueString)
            ) {
                Text(text = "True")
            }
            RadioButtonRow(
                selected = !isTrue.toBooleanStrict(),
                onClick = {
                    isTrue = falseString
                    viewModel.edited(entry = entry, change = falseString)
                },
                outline = viewModel.outline(entry = entry, value = falseString)
            ) {
                Text(text = "False")
            }
        }
    }
}