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

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.favorites.Favorite
import com.charlesmuchene.prefeditor.favorites.FavoritesCodec
import com.charlesmuchene.prefeditor.files.PrefEditorFiles

/**
 * Application preferences
 */
class AppPreferences(
    private val codec: PreferenceCodec = PreferenceCodec(),
    private val editor: PreferenceEditor = PreferenceEditor(),
    private val favoritesCodec: FavoritesCodec = FavoritesCodec(codec),
) {

    /**
     * Read a developer's favorites
     *
     * @return A [List] of [Favorite]s
     */
    suspend fun readFavorites(): List<Favorite> = buildList {
        // TODO Convert to IO op
        val path = PrefEditorFiles.preferencePath()
        addAll(favoritesCodec.decode(path = path))
    }

    /**
     * Write the developer's favorites
     *
     * @param favorites A [List] of [Favorite]s to add
     */
    suspend fun writeFavorites(favorites: List<Favorite>) {
        val content = favoritesCodec.encode(favorites = favorites, block = Edit::Add)
        val path = PrefEditorFiles.preferencePath()
        editor.edit(edits = content, path = path)
    }

    /**
     * Remove the developer's favorites
     *
     * @param favorites A [List] of [Favorite]s to remove
     */
    suspend fun removeFavorites(favorites: List<Favorite>) {
        val content = favoritesCodec.encode(favorites = favorites, block = Edit::Delete)
        val path = PrefEditorFiles.preferencePath()
        val output = editor.edit(edits = content, path = path)
        println(output)
    }

}