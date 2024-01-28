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

package com.charlesmuchene.prefeditor.screens.preferences

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.providers.LocalBridge
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.PrefKey
import com.charlesmuchene.prefeditor.screens.preferences.PrefEditorViewModel.UIState
import com.charlesmuchene.prefeditor.screens.preferences.editor.Editor
import com.charlesmuchene.prefeditor.ui.Loading
import com.charlesmuchene.prefeditor.ui.FullScreenText

@Composable
fun PrefEditor(prefFile: PrefFile, app: App, device: Device, modifier: Modifier = Modifier) {
    val bridge = LocalBridge.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PrefEditorViewModel(prefFile = prefFile, app = app, device = device, scope = scope, bridge = bridge)
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        is UIState.Error -> PrefError(modifier = modifier, message = (state as UIState.Error).message)
        UIState.Loading -> PrefLoading(modifier = modifier)
        is UIState.Success -> Editor(
            app = app,
            device = device,
            modifier = modifier,
            prefFile = prefFile,
            preferences = (state as UIState.Success).preferences,
        )
    }
}

@Composable
private fun PrefLoading(modifier: Modifier = Modifier) {
    Loading(modifier = modifier, text = LocalBundle.current[PrefKey.PrefLoading])
}

@Composable
private fun PrefError(message: String?, modifier: Modifier = Modifier) {
    FullScreenText(key = PrefKey.PrefError, secondary = message, modifier = modifier)
}