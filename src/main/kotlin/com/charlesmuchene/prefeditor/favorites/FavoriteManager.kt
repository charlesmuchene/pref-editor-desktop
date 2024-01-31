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
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.favorites.Favorite.*
import com.charlesmuchene.prefeditor.favorites.FavoriteManager.Tags.APP
import com.charlesmuchene.prefeditor.favorites.FavoriteManager.Tags.DEVICE
import com.charlesmuchene.prefeditor.favorites.FavoriteManager.Tags.FILE
import org.xmlpull.v1.XmlPullParser

class FavoriteManager {

    fun toEdits(favorites: List<Favorite>): List<Edit.Add> = favorites.map { favorite ->
        when (favorite) {
            is Device -> Edit.Add(name = DEVICE, value = favorite.serial, attributes = emptyList())
            is File -> Edit.Add(
                value = favorite.name,
                name = FILE,
                attributes = listOf(
                    Edit.Attribute(name = DEVICE, value = favorite.device.serial),
                    Edit.Attribute(name = APP, value = favorite.app.packageName),
                ),
            )

            is App -> Edit.Add(
                name = APP,
                attributes = listOf(Edit.Attribute(name = DEVICE, value = favorite.device.serial)),
                value = favorite.packageName,
            )
        }
    }

    fun read(parser: XmlPullParser) = buildList {
        when (parser.name) {
            DEVICE -> add(parser.parseDevice())
            FILE -> add(parser.parseFile())
            APP -> add(parser.parseApp())
            else -> parser.skip()
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