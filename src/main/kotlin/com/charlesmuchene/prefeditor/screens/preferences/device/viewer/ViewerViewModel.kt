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

package com.charlesmuchene.prefeditor.screens.preferences.device.viewer

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.data.Preference
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ViewerViewModel(prefUseCase: DevicePreferencesUseCase, scope: CoroutineScope) {
    private val _preferences = mutableStateOf(emptyList<Preference>())
    val preferences: State<List<Preference>> = _preferences

    init {
        prefUseCase.preferences.onEach { prefs ->
            _preferences.value = prefs.preferences
        }.launchIn(scope = scope)
    }
}
