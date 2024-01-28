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

package com.charlesmuchene.prefeditor.screens.preferences.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.PrefKey
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.PrimitiveEntry
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.rows.SetEntryRow
import com.charlesmuchene.prefeditor.ui.Toast
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*

@Composable
fun Editor(preferences: Preferences, prefFile: PrefFile, app: App, device: Device, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val navigation = LocalNavigation.current
    val viewModel = remember {
        EditorViewModel(
            app = app,
            scope = scope,
            device = device,
            bridge = bridge,
            prefFile = prefFile,
            navigation = navigation,
            preferences = preferences,
        )
    }

    val (setEntries, primitiveEntries) = viewModel.prefs

    val state = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        EditorTopBar(viewModel = viewModel)
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            Column {
                LazyColumn(modifier = Modifier.padding(end = padding), state = state) {
                    primitives(primitiveEntries = primitiveEntries, viewModel = viewModel)
                    sets(setEntries = setEntries, viewModel = viewModel)
                }
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = state),
                modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
            )
        }
    }

    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun EditorTopBar(viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier.padding(end = padding)) {
        Text(text = LocalBundle.current[PrefKey.PrefTitle], style = Typography.heading)
        Spacer(modifier = Modifier.weight(1f))
        var checked by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CheckboxRow(text = "Backup original file", checked = checked, onCheckedChange = {
                viewModel.backup(it)
                checked = it
            })
            Spacer(modifier = Modifier.width(12.dp))
            val enabled by viewModel.enableSave.collectAsState()
            DefaultButton(onClick = viewModel::save, enabled = enabled) {
                Text(text = "Save")
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.sets(
    setEntries: List<Entry>,
    viewModel: EditorViewModel,
) {
    if (setEntries.isEmpty()) return

    item {
        Divider(
            orientation = Orientation.Horizontal,
            color = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }

    stickyHeader {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "String sets", style = Typography.heading)
            Text(
                text = "(editing not yet supported)",
                style = Typography.secondary,
                color = Color.DarkGray
            )
        }
    }

    items(items = setEntries /* FIXME Define stable keys */) { entry ->
        SetEntryRow(
            entry as SetEntry,
            viewModel = viewModel,
            modifier = Modifier.padding(end = 18.dp).fillMaxWidth().height((64 * entry.entries.size).dp),
        )
    }
}

private fun LazyListScope.primitives(
    primitiveEntries: List<Entry>,
    viewModel: EditorViewModel,
) {
    items(items = primitiveEntries /* FIXME Define stable keys */) { entry ->
        val entryModifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        PrimitiveEntry(entry = entry, modifier = entryModifier, viewModel = viewModel)
    }
}
