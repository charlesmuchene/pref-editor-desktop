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

package com.charlesmuchene.prefeditor.app

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.preferences.AppPreferences
import com.charlesmuchene.prefeditor.ui.theme.PrefEditorTheme
import com.charlesmuchene.prefeditor.ui.theme.PrefEditorTheme.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AppState {

    var theme: PrefEditorTheme by mutableStateOf(System)

    val windowSize = DpSize(width = 1020.dp, height = 800.dp)

    private val _toastMessage = MutableSharedFlow<String?>()
    val toastMessage: SharedFlow<String?> = _toastMessage

    val preferences = AppPreferences()

    suspend fun showToast(message: String) {
        _toastMessage.emit(message)
    }

    fun switchTheme() {
        theme = when (theme) {
            Light -> Dark
            Dark -> System
            System -> Light
        }
    }
}