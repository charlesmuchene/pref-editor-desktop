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

package com.charlesmuchene.prefedit.app

import com.charlesmuchene.prefedit.navigation.Home
import com.charlesmuchene.prefedit.navigation.Screen
import com.charlesmuchene.prefedit.ui.theme.PrefEditTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppState {
    private val _screen = MutableStateFlow<Screen>(Home)

    // TODO Theme components dark
    val theme: PrefEditTheme = PrefEditTheme.Light

    val screen: StateFlow<Screen> = _screen.asStateFlow()

    suspend fun navigateTo(screen: Screen) {
        _screen.emit(screen)
    }
}