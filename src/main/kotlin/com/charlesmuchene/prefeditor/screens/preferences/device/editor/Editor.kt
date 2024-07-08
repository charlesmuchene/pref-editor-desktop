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

package com.charlesmuchene.prefeditor.screens.preferences.device.editor

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.datastore.preferences.StringSetPreference
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.PrefsKey
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import com.charlesmuchene.prefeditor.ui.APP_HALF_SPACING
import com.charlesmuchene.prefeditor.ui.Toast
import com.charlesmuchene.prefeditor.ui.theme.Typography
import com.charlesmuchene.prefeditor.ui.theme.appGray
import kotlinx.coroutines.launch
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.CheckboxRow
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Editor(
    prefUseCase: DevicePreferencesUseCase,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val appState = LocalAppState.current
    val viewModel =
        remember {
            EditorViewModel(appState = appState, scope = scope, prefUseCase = prefUseCase)
        }

    val prefs by viewModel.preferences.collectAsState()
    val partition by remember(prefs) { mutableStateOf(prefs.partition { it.preference is StringSetPreference }) }
    val (sets, primitives) = partition

    Column(modifier = modifier.fillMaxSize()) {
        EditorTopBar(viewModel = viewModel, modifier = Modifier)
        Spacer(modifier = Modifier.height(APP_HALF_SPACING))
        Divider(orientation = Orientation.Horizontal, color = appGray)
        Box(modifier = Modifier.fillMaxSize()) {
            val state = rememberLazyListState()
            LazyColumn(modifier = Modifier.padding(end = APP_HALF_SPACING), state = state) {
                primitives(preferences = primitives, viewModel = viewModel)
                sets(preferences = sets, viewModel = viewModel)
            }
            VerticalScrollbar(
                adapter = rememberScrollbarAdapter(scrollState = state),
                modifier = Modifier.align(Alignment.CenterEnd).offset(x = APP_HALF_SPACING).fillMaxHeight(),
            )
        }
    }

    val message by viewModel.message.collectAsState(initial = null)
    message?.let { Toast(text = it) }
}

@Composable
private fun EditorTopBar(
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier,
) {
    var showAddPreference by remember { mutableStateOf(false) }

    Row(modifier = modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = LocalBundle.current[PrefsKey.PrefTitle], style = Typography.heading)
        val checked by viewModel.backupEnabled
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedButton(onClick = { showAddPreference = true }, modifier = Modifier.pointerOnHover()) {
                val text = "Add preference"
                val painter by rememberIconPainter(name = "plus")
                Icon(
                    painter = painter,
                    contentDescription = text,
                    tint = JewelTheme.contentColor,
                    modifier = Modifier.size(14.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = text)
            }
            Spacer(modifier = Modifier.width(APP_HALF_SPACING))
            CheckboxRow(text = "Backup file", checked = checked, onCheckedChange = viewModel::backup)
            Spacer(modifier = Modifier.width(APP_HALF_SPACING))
            val enabled by viewModel.enableSave.collectAsState()
            DefaultButton(onClick = viewModel::save, enabled = enabled) {
                Text(text = "Save")
            }
        }
    }

    if (showAddPreference) {
        val scope = rememberCoroutineScope()
        AddPreferenceComponent(onDismiss = { showAddPreference = false }, onAdd = { name, value, type ->
            scope.launch {
                showAddPreference = !viewModel.add(name = name, value = value, type = type)
            }
        })
    }
}

private fun LazyListScope.sets(
    preferences: List<UIPreference>,
    viewModel: EditorViewModel,
) {
    if (preferences.isEmpty()) return
    setPreferenceSection(preferences = preferences, viewModel = viewModel)
}

@OptIn(ExperimentalFoundationApi::class)
private fun LazyListScope.primitives(
    preferences: List<UIPreference>,
    viewModel: EditorViewModel,
) {
    items(items = preferences, key = { it.preference.key }) { preference ->
        val modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp)
        PrimitivePreference(preference = preference, viewModel = viewModel, modifier = modifier.animateItemPlacement())
    }
}
