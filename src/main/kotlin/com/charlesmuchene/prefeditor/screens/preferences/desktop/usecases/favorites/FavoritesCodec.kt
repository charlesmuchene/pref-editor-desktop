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

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.Favorite.*
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.APP
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.DEVICE
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.FILE
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.NAME
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.PACKAGE
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites.FavoritesCodec.Tags.SERIAL
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.nio.file.Path
import kotlin.io.path.inputStream

class FavoritesCodec(private val codec: PreferencesCodec) {

    fun encode(favorite: Favorite, block: (String) -> Edit): Edit = when (favorite) {
        is Device -> block(codec.encode { serializeDevice(favorite) })
        is File -> block(codec.encode { serializeFile(favorite) })
        is App -> block(codec.encode { serializeApp(favorite) })
    }

    private fun XmlSerializer.serializeDevice(favorite: Device) {
        tag(DEVICE) { attrib(name = SERIAL, value = favorite.serial) }
    }

    private fun XmlSerializer.serializeApp(favorite: App) {
        tag(APP) {
            attrib(name = DEVICE, value = favorite.device)
            attrib(name = PACKAGE, value = favorite.packageName)
        }
    }

    private fun XmlSerializer.serializeFile(favorite: File) {
        tag(FILE) {
            attrib(name = DEVICE, value = favorite.device)
            attrib(name = APP, value = favorite.app)
            attrib(name = NAME, value = favorite.name)
        }
    }

    suspend fun decode(path: Path) = buildList {
        codec.decode(path.inputStream()) {
            when (name) {
                DEVICE -> add(parseDevice())
                FILE -> add(parseFile())
                APP -> add(parseApp())
                else -> skip()
            }
        }
    }

    private fun XmlPullParser.parseDevice(): Device = gobbleTag(DEVICE) { Device(serial = getAttributeValue(0)) }

    private fun XmlPullParser.parseApp(): App = gobbleTag(APP) {
        val serial = getAttributeValue(0)
        val packageName = getAttributeValue(1)
        App(device = serial, packageName = packageName)
    }

    private fun XmlPullParser.parseFile(): File = gobbleTag(FILE) {
        val serial = getAttributeValue(0)
        val packageName = getAttributeValue(1)
        val name = getAttributeValue(2)
        File(device = serial, app = packageName, name = name)
    }

    private object Tags {
        const val APP = "app"
        const val FILE = "file"
        const val NAME = "name"
        const val DEVICE = "device"
        const val SERIAL = "serial"
        const val PACKAGE = "package"
    }
}