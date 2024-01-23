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

package com.charlesmuchene.prefedit.screens.preferences.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.Entry
import com.charlesmuchene.prefedit.data.Preferences
import com.charlesmuchene.prefedit.data.SetEntry
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.PrefKey
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.PrimitiveEntry
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.rows.SetEntryRow
import com.charlesmuchene.prefedit.ui.padding
import com.charlesmuchene.prefedit.ui.theme.Typography
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Editor(preferences: Preferences, modifier: Modifier = Modifier) {

    val text = LocalBundle.current[PrefKey.PrefTitle] // TODO Display file name under edit
    val scope = rememberCoroutineScope()
    val viewModel = remember { EditorViewModel(preferences = preferences, scope = scope) }

    val (setEntries, primitiveEntries) = viewModel.prefs

    val state = rememberLazyListState()

    Column(modifier = modifier.fillMaxSize()) {
        Text(text = text, style = Typography.heading)
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
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.sets(
    setEntries: List<Entry>,
    viewModel: EditorViewModel,
) {
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
            modifier = Modifier.padding(end = 18.dp).fillMaxWidth().height(200.dp),
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
