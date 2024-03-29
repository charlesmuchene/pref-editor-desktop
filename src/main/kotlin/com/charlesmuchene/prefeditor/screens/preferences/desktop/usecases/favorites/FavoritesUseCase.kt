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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

private val logger = KotlinLogging.logger {}

@Suppress("TooManyFunctions")
class FavoritesUseCase(
    private val path: Path,
    private val codec: FavoritesCodec,
    private val writer: PreferenceWriter,
    private val context: CoroutineContext = Dispatchers.Default,
) : CoroutineScope by CoroutineScope(context) {
    private lateinit var favorites: List<Favorite>

    suspend fun refresh() {
        favorites = codec.decode(path = path)
    }

    fun isFavorite(device: Device): Boolean =
        favorites.filterIsInstance<Favorite.Device>()
            .any { it.serial == device.serial }

    fun isFavorite(
        app: App,
        device: Device,
    ): Boolean =
        favorites.filterIsInstance<Favorite.App>().any {
            it.device == device.serial && it.packageName == app.packageName
        }

    fun isFavorite(
        file: PrefFile,
        app: App,
        device: Device,
    ): Boolean =
        favorites.filterIsInstance<Favorite.File>().any {
            it.device == device.serial && it.app == app.packageName && it.name == file.name
        }

    private suspend fun writeFavorite(favorite: Favorite) {
        val edit = codec.encode(favorite = favorite, block = { Edit.Add(content = it) })
        writer.edit(edit = edit)
        refresh()
    }

    private suspend fun removeFavorite(favorite: Favorite) {
        val edit = codec.encode(favorite = favorite, block = Edit::Delete)
        writer.edit(edit = edit)
        refresh()
    }

    suspend fun favoriteApp(
        app: App,
        device: Device,
    ) {
        writeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun unfavoriteApp(
        app: App,
        device: Device,
    ) {
        removeFavorite(favorite = Favorite.App(device = device.serial, packageName = app.packageName))
    }

    suspend fun favoriteDevice(device: Device) {
        writeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun unfavoriteDevice(device: Device) {
        removeFavorite(favorite = Favorite.Device(device.serial))
    }

    suspend fun favoriteFile(
        file: PrefFile,
        app: App,
        device: Device,
    ) {
        writeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }

    suspend fun unfavoriteFile(
        file: PrefFile,
        app: App,
        device: Device,
    ) {
        removeFavorite(favorite = Favorite.File(device = device.serial, app = app.packageName, file.name))
    }
}
