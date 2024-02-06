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
import com.charlesmuchene.prefeditor.data.SetPreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.SetSubPreference
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.lazy.tree.buildTree
import org.jetbrains.jewel.ui.component.LazyTree
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField


@OptIn(ExperimentalJewelApi::class)
@Composable
fun SetPreferenceRow(preference: SetPreference, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    val subPrefs = viewModel.createSubPreferences(preference)
    val tree = remember {
        buildTree {
            addNode(subPrefs.first) {
                subPrefs.second.forEach(::addLeaf)
            }
        }
    }

    // FIXME: Make an editable preference
    // TODO Remove selection highlighting
    LazyTree(
        tree = tree,
        modifier = modifier,
    ) { element ->
        when (val data = element.data) {
            is SetSubPreference.Header -> SetSubPreferenceHeaderRow(data)
            is SetSubPreference.Preference -> SetSubPreferenceRow(data)
        }
    }
}

@Composable
private fun SetSubPreferenceHeaderRow(header: SetSubPreference.Header, modifier: Modifier = Modifier) {
    Text(text = header.name, style = Typography.secondary, modifier = modifier)
}

@Composable
private fun SetSubPreferenceRow(preference: SetSubPreference.Preference, modifier: Modifier = Modifier) {
    var value by remember { mutableStateOf(preference.value) }
    TextField(
        modifier = modifier,
        value = value,
        onValueChange = { value = it },
        placeholder = { Text(text = preference.value) },
        keyboardOptions = KeyboardOptions(autoCorrect = false),
    )
}