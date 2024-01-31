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

package com.charlesmuchene.prefeditor.favorites

import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.preferences.AppPreferences

class FavoritesUseCase(private val preferences: AppPreferences) {

    private val favorites by lazy { preferences.readFavorites() }

    fun isFavorite(device: Device): Boolean =
        favorites.filterIsInstance<Favorite.Device>().map(Favorite.Device::serial).contains(device.serial)

    suspend fun writeFavorites(favorites: List<Favorite>) {
        preferences.writeFavorites(favorites = favorites)
    }

    suspend fun removeFavorites(favorites: List<Favorite>) {
        preferences.removeFavorites(favorites = favorites)
    }
}