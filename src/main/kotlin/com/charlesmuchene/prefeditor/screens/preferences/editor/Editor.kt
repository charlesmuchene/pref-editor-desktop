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
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalNavigation
import com.charlesmuchene.prefeditor.resources.PrefKey
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.PrimitivePreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.setPreferenceSection
import com.charlesmuchene.prefeditor.ui.Toast
import com.charlesmuchene.prefeditor.ui.padding
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Editor(preferences: Preferences, prefFile: PrefFile, app: App, device: Device, prefUseCase: DevicePreferencesUseCase, modifier: Modifier = Modifier) {

    val scope = rememberCoroutineScope()
    val bridge = LocalBridge.current
    val appState = LocalAppState.current
    val navigation = LocalNavigation.current
    val viewModel = remember {
        EditorViewModel(
            app = app,
            scope = scope,
            device = device,
            bridge = bridge,
            prefFile = prefFile,
            appState = appState,
            navigation = navigation,
            preferences = preferences,
            prefUseCase = prefUseCase,
        )
    }

    val prefs by viewModel.entries
    val partition by remember(prefs) { mutableStateOf(prefs.partition { it.preference is SetPreference }) }
    val (sets, primitives) = partition

    Column(modifier = modifier.fillMaxSize()) {
        val endPadding = padding * 0.5f
        EditorTopBar(viewModel = viewModel, modifier = Modifier.padding(end = endPadding))
        Spacer(modifier = Modifier.height(4.dp))
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()
            LazyColumn(modifier = Modifier.padding(end = endPadding), state = state) {
                primitives(preferences = primitives, viewModel = viewModel)
                sets(preferences = sets, viewModel = viewModel)
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = state),
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = endPadding).fillMaxHeight(),
            )
        }
    }

    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun EditorTopBar(viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = LocalBundle.current[PrefKey.PrefTitle], style = Typography.heading)
        Spacer(modifier = Modifier.weight(1f))
        var checked by remember { mutableStateOf(false) }
        Row(verticalAlignment = Alignment.CenterVertically) {
            CheckboxRow(text = "Backup file", checked = checked, onCheckedChange = {
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

private fun LazyListScope.sets(preferences: List<UIPreference>, viewModel: EditorViewModel) {
    if (preferences.isEmpty()) return
    setPreferenceSection(preferences = preferences, viewModel = viewModel)
}

private fun LazyListScope.primitives(preferences: List<UIPreference>, viewModel: EditorViewModel) {
    items(items = preferences, key = { it.preference.name }) { preference ->
        val modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        PrimitivePreference(preference = preference, modifier = modifier, viewModel = viewModel)
    }
}
