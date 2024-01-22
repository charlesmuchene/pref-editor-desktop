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

package com.charlesmuchene.prefedit.screens.prefs

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.PrefFile
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.PrefKey
import com.charlesmuchene.prefedit.screens.prefs.PrefEditorViewModel.UIState
import com.charlesmuchene.prefedit.ui.Loading
import com.charlesmuchene.prefedit.ui.SingleText

@Composable
fun PrefEditor(prefFile: PrefFile, app: App, device: Device, modifier: Modifier = Modifier) {
    val bridge = LocalBridge.current
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PrefEditorViewModel(prefFile = prefFile, app = app, device = device, scope = scope, bridge = bridge)
    }
    val state by viewModel.uiState.collectAsState()

    when (state) {
        UIState.Error -> PrefError(modifier = modifier)
        UIState.Loading -> PrefLoading(modifier = modifier)
        is UIState.Preferences -> Editor(pref = (state as UIState.Preferences).pref, modifier = modifier)
    }
}

@Composable
private fun PrefLoading(modifier: Modifier = Modifier) {
    Loading(modifier = modifier, text = LocalBundle.current[PrefKey.PrefLoading])
}

@Composable
private fun PrefError(modifier: Modifier = Modifier) {
    SingleText(key = PrefKey.PrefError, modifier = modifier)
}