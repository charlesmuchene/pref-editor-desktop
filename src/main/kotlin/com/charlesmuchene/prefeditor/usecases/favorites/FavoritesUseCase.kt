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
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

class FavoritesUseCase(
    private val path: Path = EditorFiles.preferencesPath(),
    private val editor: PreferenceEditor = PreferenceEditor(),
    private val codec: FavoritesCodec = FavoritesCodec(PreferencesCodec()),
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by CoroutineScope(context) {

    private lateinit var favorites: List<Favorite>

    init {
        launch { refresh() }
    }

    private suspend fun refresh() {
        favorites = codec.decode(path = path)
    }

    fun isFavorite(app: App): Boolean =
        favorites.filterIsInstance<Favorite.App>().map(Favorite.App::packageName).contains(app.packageName)

    fun isFavorite(device: Device): Boolean =
        favorites.filterIsInstance<Favorite.Device>().map(Favorite.Device::serial).contains(device.serial)

    fun isFavorite(file: PrefFile): Boolean =
        favorites.filterIsInstance<Favorite.File>().map(Favorite.File::name).contains(file.name)

    private suspend fun writeFavorite(favorite: Favorite) {
        writeFavorites(favorites = listOf(favorite))
    }

    private suspend fun removeFavorite(favorite: Favorite) {
        removeFavorites(listOf(element = favorite))
    }

    private suspend fun writeFavorites(favorites: List<Favorite>) {
        val content = codec.encode(favorites = favorites, block = Edit::Add)
        editor.edit(edits = content, path = path)
        refresh()
    }

    private suspend fun removeFavorites(favorites: List<Favorite>) {
        val content = codec.encode(favorites = favorites, block = Edit::Delete)
        val path = EditorFiles.preferencesPath()
        val output = editor.edit(edits = content, path = path)
        logger.debug { "Remove favorites: $output" }
    }

    suspend fun favoriteApp(app: App, device: Device) {
        writeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun unfavoriteApp(app: App, device: Device) {
        writeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun favoriteDevice(device: Device) {
        writeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun unfavoriteDevice(device: Device) {
        removeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun favoritePrefFile(file: PrefFile, app: App, device: Device) {
        writeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }

    suspend fun unfavoritePrefFile(file: PrefFile, app: App, device: Device) {
        removeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }
}