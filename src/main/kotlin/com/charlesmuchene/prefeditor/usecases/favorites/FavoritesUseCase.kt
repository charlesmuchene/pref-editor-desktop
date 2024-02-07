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

package com.charlesmuchene.prefeditor.usecases.favorites

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.preferences.PreferenceWriter
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

class FavoritesUseCase(
    private val codec: FavoritesCodec,
    private val editor: PreferenceWriter,
    private val path: Path = EditorFiles.preferencesPath(),
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by CoroutineScope(context) {

    private lateinit var favorites: List<Favorite>

    suspend fun refresh() {
        favorites = codec.decode(path = path)
    }

    fun isFavorite(app: App, device: Device): Boolean =
        favorites.filterIsInstance<Favorite.App>().any {
            it.device == device.serial && it.packageName == app.packageName
        }

    fun isFavorite(device: Device): Boolean =
        favorites.filterIsInstance<Favorite.Device>().any { it.serial == device.serial }

    fun isFavorite(file: PrefFile, app: App, device: Device): Boolean =
        favorites.filterIsInstance<Favorite.File>().any {
            it.device == device.serial && it.app == app.packageName && it.name == file.name
        }

    private suspend fun writeFavorite(favorite: Favorite) {
        writeFavorites(favorites = listOf(favorite))
    }

    private suspend fun removeFavorite(favorite: Favorite) {
        removeFavorites(listOf(element = favorite))
    }

    private suspend fun writeFavorites(favorites: List<Favorite>) {
        val content = codec.encode(favorites = favorites, block = Edit::Add)
        editor.edit(edits = content)
        refresh()
    }

    private suspend fun removeFavorites(favorites: List<Favorite>) {
        val content = codec.encode(favorites = favorites, block = Edit::Delete)
        val output = editor.edit(edits = content)
        if (output.isNotBlank()) logger.debug { "Remove favorites: $output" }
        refresh()
    }

    suspend fun favoriteApp(app: App, device: Device) {
        writeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun unfavoriteApp(app: App, device: Device) {
        removeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun favoriteDevice(device: Device) {
        writeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun unfavoriteDevice(device: Device) {
        removeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun favoriteFile(file: PrefFile, app: App, device: Device) {
        writeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }

    suspend fun unfavoriteFile(file: PrefFile, app: App, device: Device) {
        removeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }
}