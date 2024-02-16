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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.extensions.screenTransitionSpec
import com.charlesmuchene.prefeditor.navigation.EditScreen
import com.charlesmuchene.prefeditor.providers.LocalBundle
import com.charlesmuchene.prefeditor.providers.LocalReloadSignal
import com.charlesmuchene.prefeditor.resources.PrefKey
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferencesViewModel.UIState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.Editor
import com.charlesmuchene.prefeditor.screens.preferences.device.viewer.Viewer
import com.charlesmuchene.prefeditor.ui.FullScreenText
import com.charlesmuchene.prefeditor.ui.Loading

@Composable
fun PreferencesScreen(
    screen: EditScreen,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val reloadSignal = LocalReloadSignal.current

    val viewModel =
        remember {
            PreferencesViewModel(
                scope = scope,
                app = screen.app,
                device = screen.device,
                prefFile = screen.file,
                readOnly = screen.readOnly,
                reloadSignal = reloadSignal,
            )
        }
    val uiState by viewModel.uiState.collectAsState()

    AnimatedContent(targetState = uiState, transitionSpec = { screenTransitionSpec() }) { state ->
        when (state) {
            UIState.Loading -> PrefLoading(modifier = modifier)
            is UIState.Error -> PrefError(modifier = modifier, message = state.message)
            is UIState.Success ->
                if (state.readOnly) {
                    Viewer(
                        prefUseCase = viewModel.useCase,
                        onEditClick = viewModel::edit,
                        modifier = modifier,
                    )
                } else {
                    Editor(modifier = modifier, prefUseCase = viewModel.useCase)
                }
        }
    }
}

@Composable
private fun PrefLoading(modifier: Modifier = Modifier) {
    Loading(modifier = modifier, text = LocalBundle.current[PrefKey.PrefLoading])
}

@Composable
private fun PrefError(
    message: String?,
    modifier: Modifier = Modifier,
) {
    val primary = LocalBundle.current[PrefKey.PrefError]
    FullScreenText(primary = primary, secondary = message, modifier = modifier)
}
