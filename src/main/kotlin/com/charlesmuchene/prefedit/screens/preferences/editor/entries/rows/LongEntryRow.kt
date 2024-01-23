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

package com.charlesmuchene.prefedit.screens.preferences.editor.entries.rows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import com.charlesmuchene.prefedit.data.LongEntry
import com.charlesmuchene.prefedit.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.componentSpacing
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.nameComponentWeight
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.valueComponentWeight
import com.charlesmuchene.prefedit.ui.theme.Typography
import org.jetbrains.jewel.ui.Outline
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun LongEntryRow(entry: LongEntry, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(componentSpacing)) {
        Text(text = entry.name, style = Typography.primary, modifier = Modifier.weight(nameComponentWeight))
        val initialValue = entry.value.toString()
        var value by remember { mutableStateOf(initialValue) }
        var outline by remember { mutableStateOf(Outline.None) }
        TextField(
            value = value,
            outline = outline,
            onValueChange = { changed ->
                value = changed
                outline = viewModel.outline(entry, value)
            },
            placeholder = { Text(text = initialValue) },
            modifier = Modifier.weight(valueComponentWeight),
            keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = KeyboardType.Number),
        )
    }
}