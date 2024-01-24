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

package com.charlesmuchene.prefedit.navigation

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class Navigation(private val scope: CoroutineScope) : CoroutineScope by scope {

    private val stack = mutableListOf<Screen>(HomeScreen)
    private val _screens = MutableSharedFlow<List<Screen>>()

    val screens: StateFlow<List<Screen>> = _screens
        .stateIn(scope = scope, started = SharingStarted.WhileSubscribed(), initialValue = stack)

    fun navigate(screen: Screen) {
        when (val index = stack.indexOf(screen)) {
            -1 -> navigateForwardTo(screen)
            0 -> navigateHome()
            else -> navigateBackwardTo(screen, index)
        }
    }

    private fun navigateHome() {
        if (stack.size == 1) return
        stack.clear()
        stack.add(HomeScreen)
        updateObservers()
    }

    private fun navigateForwardTo(screen: Screen) {
        stack.add(screen)
        updateObservers()
    }

    private fun navigateBackwardTo(screen: Screen, index: Int) {
        if (stack.lastIndex == index) return
        stack.removeAll(stack.subList(index + 1, stack.size))
        updateObservers()
    }

    private fun updateObservers() {
        launch { _screens.emit(stack) }
    }
}