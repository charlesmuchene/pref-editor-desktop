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
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.EntryAction
import com.charlesmuchene.prefeditor.screens.preferences.editor.UIEntry
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.ACTION_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.NAME_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.VALUE_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.componentSpacing
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun PrimitiveEntryRow(
    entry: UIEntry,
    viewModel: EditorViewModel,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing)
    ) {
        Text(text = entry.entry.name, style = Typography.secondary, modifier = Modifier.weight(NAME_COMPONENT_WEIGHT))
        var localUIEntry by remember(entry) { mutableStateOf(entry) }
        val outline by remember(localUIEntry) { mutableStateOf(viewModel.outline(entry = localUIEntry.entry)) }

        TextField(
            outline = outline,
            value = localUIEntry.entry.value,
            placeholder = { Text(text = entry.entry.value) },
            modifier = Modifier.weight(VALUE_COMPONENT_WEIGHT),
            keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = keyboardType),
            onValueChange = { changed ->
                val change = EntryAction.Change(entry = localUIEntry.entry, change = changed)
                localUIEntry = viewModel.entryAction(change)
            },
        )

        EntryAction(
            onEntryAction = { localUIEntry = viewModel.entryAction(it) },
            modifier = Modifier.weight(ACTION_COMPONENT_WEIGHT),
            uiEntry = localUIEntry,
        )
    }
}