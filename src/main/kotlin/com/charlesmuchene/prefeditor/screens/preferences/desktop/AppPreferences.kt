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

package com.charlesmuchene.prefeditor.screens.preferences.desktop

import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.ThemeCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.ThemeUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Application preferences
 */
class AppPreferences(codec: PreferencesCodec, editor: PreferenceWriter) {
    val favorites = FavoritesUseCase(editor = editor, codec = FavoritesCodec(codec))
    val theme = ThemeUseCase(editor = editor, codec = ThemeCodec(codec))

    suspend fun initialize() = coroutineScope {
        awaitAll(async { favorites.refresh() }, async { theme.loadTheme() })
    }
}