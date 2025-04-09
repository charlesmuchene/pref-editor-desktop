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

import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.prefeditor.data.DatastorePreferences
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import com.charlesmuchene.prefeditor.screens.preferences.device.DevicePreferencesUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class ViewerViewModel(prefUseCase: DevicePreferencesUseCase, scope: CoroutineScope) {
    private val _preferences = MutableStateFlow(emptyList<Preference>())
    val preferences: StateFlow<List<Preference>> = _preferences.asStateFlow()

    init {
        scope.launch {
            prefUseCase.preferences
                .map {
                    when (it) {
                        is DatastorePreferences -> it.parse()
                        is KeyValuePreferences -> it.preferences
                    }
                }
                .collect(_preferences)
        }
    }
}
