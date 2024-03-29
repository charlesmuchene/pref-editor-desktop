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
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.screens.preferences.desktop.AppPreferences
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.EditorTheme
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AppState(val preferences: AppPreferences) {
    lateinit var executable: String

    val favorites = preferences.favorites
    val theme: EditorTheme by preferences.theme.theme

    val windowSize = DpSize(width = 1020.dp, height = 800.dp)

    private val _toastMessage = MutableSharedFlow<String?>()
    val toastMessage: SharedFlow<String?> = _toastMessage

    suspend fun showToast(message: String) {
        _toastMessage.emit(message)
    }

    fun changeTheme(theme: EditorTheme) {
        preferences.theme.changeTheme(theme)
    }
}
