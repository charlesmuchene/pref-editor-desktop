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

package com.charlesmuchene.prefeditor.screens.preferences.device

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.updateTransition
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.resources.PrefKey
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferencesViewModel.UIState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.Editor
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.Loading

@Composable
fun PreferencesScreen(prefFile: PrefFile, app: App, device: Device, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val viewModel = remember {
        PreferencesViewModel(app = app, device = device, prefFile = prefFile, scope = scope)
    }
    val uiState by viewModel.uiState.collectAsState()

    updateTransition(uiState).AnimatedContent(transitionSpec = { screenTransitionSpec() }) { state ->
        when (state) {
            UIState.Loading -> PrefLoading(modifier = modifier)
            is UIState.Error -> PrefError(modifier = modifier, message = state.message)
            is UIState.Success -> Editor(modifier = modifier, prefUseCase = viewModel.useCase)
        }
    }
}

@Composable
private fun PrefLoading(modifier: Modifier = Modifier) {
    Loading(modifier = modifier, text = LocalBundle.current[PrefKey.PrefLoading])
}

@Composable
private fun PrefError(message: String?, modifier: Modifier = Modifier) {
    val primary = LocalBundle.current[PrefKey.PrefError]
    FullScreenText(primary = primary, secondary = message, modifier = modifier)
}