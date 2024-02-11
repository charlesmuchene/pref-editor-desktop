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

package com.charlesmuchene.prefeditor.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class Navigation(private val scope: CoroutineScope) : CoroutineScope by scope {

    private val stack = mutableListOf<Screen>(DevicesScreen)

    val screens: List<Screen> get() = stack

    private val _currentScreen = MutableStateFlow<Screen>(DevicesScreen)
    val current: StateFlow<Screen> = _currentScreen.asStateFlow()

    fun navigate(screen: Screen) {
        when (val index = stack.indexOf(screen)) {
            -1 -> navigateForwardTo(screen)
            0 -> navigateHome()
            else -> navigateBackwardTo(index)
        }
    }

    private fun navigateHome() {
        if (stack.size == 1) return
        stack.clear()
        stack.add(DevicesScreen)
        updateObservers()
    }

    private fun navigateForwardTo(screen: Screen) {
        stack.add(screen)
        updateObservers()
    }

    private fun navigateBackwardTo(index: Int) {
        if (stack.lastIndex == index) return
        stack.removeAll(stack.subList(index + 1, stack.size))
        updateObservers()
    }

    private fun updateObservers() {
        launch { _currentScreen.emit(stack.last()) }
    }
}