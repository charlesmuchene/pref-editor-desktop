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

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.data.SetEntry
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.SetSubEntry
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.lazy.tree.buildTree
import org.jetbrains.jewel.ui.component.LazyTree
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField


@OptIn(ExperimentalJewelApi::class)
@Composable
fun SetEntryRow(entry: SetEntry, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    val entries = viewModel.createSubEntries(entry)
    val tree = remember {
        buildTree {
            addNode(entries.first) {
                entries.second.forEach(::addLeaf)
            }
        }
    }

    // FIXME: Make an editable entry
    // TODO Remove selection highlighting
    LazyTree(
        tree = tree,
        modifier = modifier,
    ) { element ->
        when (val data = element.data) {
            is SetSubEntry.Header -> SetSubEntryHeaderRow(data)
            is SetSubEntry.Entry -> SetSubEntryRow(data)
        }
    }
}

@Composable
private fun SetSubEntryHeaderRow(header: SetSubEntry.Header, modifier: Modifier = Modifier) {
    Text(text = header.name, style = Typography.secondary, modifier = modifier)
}

@Composable
private fun SetSubEntryRow(entry: SetSubEntry.Entry, modifier: Modifier = Modifier) {
    var value by remember { mutableStateOf(entry.value) }
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { value = it },
        placeholder = { Text(text = entry.value) },
        keyboardOptions = KeyboardOptions(autoCorrect = false),
    )
}