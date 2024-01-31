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

package com.charlesmuchene.prefeditor.preferences

import com.charlesmuchene.prefeditor.favorites.Favorite
import com.charlesmuchene.prefeditor.favorites.FavoriteManager
import com.charlesmuchene.prefeditor.files.PrefEditorFiles
import kotlin.io.path.inputStream

/**
 * Application preferences
 */
class AppPreferences(
    private val codec: PreferenceCodec = PreferenceCodec(),
    private val favoriteManager: FavoriteManager = FavoriteManager(),
    private val editor: PreferenceEditor = PreferenceEditor(encoder = codec),
) {

    /**
     * Read a developer's favorites
     *
     * @return A [List] of [Favorite]s
     */
    fun readFavorites(): List<Favorite> = buildList {
        val path = PrefEditorFiles.preferencePath()
        // TODO Convert to IO op
        codec.decode(path.inputStream()) {
            addAll(favoriteManager.read(parser = this))
        }
    }

    /**
     * Write the developer's favorites
     *
     * @param favorites A [List] of [Favorite]s
     */
    suspend fun writeFavorites(favorites: List<Favorite>) {
        val path = PrefEditorFiles.preferencePath()
        favoriteManager.toEdits(favorites = favorites).forEach { editor.edit(it, path) }
    }

}