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

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.favorites.Favorite.*
import com.charlesmuchene.prefeditor.favorites.FavoritesCodec.Tags.APP
import com.charlesmuchene.prefeditor.favorites.FavoritesCodec.Tags.DEVICE
import com.charlesmuchene.prefeditor.favorites.FavoritesCodec.Tags.FILE
import com.charlesmuchene.prefeditor.preferences.PreferenceCodec
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlSerializer
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.reflect.KClass

class FavoritesCodec(private val codec: PreferenceCodec) {

    fun encode(favorites: List<Favorite>, block: (String) -> Edit): List<Edit> = favorites.map { favorite ->
        when (favorite) {
            is Device -> block(codec.encode { serializeDevice(favorite) })
            is File -> block(codec.encode { serializeFile(favorite) })
            is App -> block(codec.encode { serializeApp(favorite) })
        }
    }

    private fun XmlSerializer.serializeDevice(favorite: Device) {
        tag(DEVICE) { text(favorite.serial) }
    }

    private fun XmlSerializer.serializeApp(favorite: App) {
        tag(APP) {
            attrib(name = DEVICE, value = favorite.device.serial)
            text(favorite.packageName)
        }
    }

    private fun XmlSerializer.serializeFile(favorite: File) {
        tag(FILE) {
            attrib(name = DEVICE, value = favorite.device.serial)
            attrib(name = APP, value = favorite.app.packageName)
            text(favorite.name)
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

    private fun XmlPullParser.parseDevice(): Device = gobbleTag(DEVICE) {
        next()
        Device(serial = text)
    }

    private fun XmlPullParser.parseApp(): App = gobbleTag(APP) {
        val serial = getAttributeValue(0)
        next()
        App(Device(serial), text)
    }

    private fun XmlPullParser.parseFile(): File = gobbleTag(FILE) {
        val serial = getAttributeValue(0)
        val packageName = getAttributeValue(1)
        next()
        val device = Device(serial)
        File(device = device, app = App(device = device, packageName = packageName), name = text)
    }

    private object Tags {
        const val APP = "app"
        const val FILE = "file"
        const val DEVICE = "device"
    }
}