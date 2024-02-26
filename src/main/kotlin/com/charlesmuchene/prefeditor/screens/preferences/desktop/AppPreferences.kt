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
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.executable.ExecutableCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.executable.ExecutableUseCase
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesUseCase
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.ThemeCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.ThemeUseCase
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path

/**
 * Application preferences
 */
class AppPreferences(codec: PreferencesCodec, writer: PreferenceWriter, path: Path) {
    val theme = ThemeUseCase(writer = writer, codec = ThemeCodec(codec), path = path)
    val executable = ExecutableUseCase(codec = ExecutableCodec(codec), prefPath = path, writer = writer)
    val favorites = FavoritesUseCase(writer = writer, codec = FavoritesCodec(codec), path = path)

    suspend fun initialize() =
        coroutineScope {
            awaitAll(
                async { favorites.refresh() },
                async { theme.loadTheme() },
                async { executable.readExecutable() },
            )
        }
}
