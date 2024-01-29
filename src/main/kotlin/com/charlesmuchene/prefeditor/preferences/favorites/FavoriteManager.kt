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

package com.charlesmuchene.prefeditor.preferences.favorites

import com.charlesmuchene.prefeditor.preferences.PreferenceReader.Reader.gobbleTag
import com.charlesmuchene.prefeditor.preferences.PreferenceReader.Reader.skip
import com.charlesmuchene.prefeditor.preferences.favorites.Favorite.*
import com.charlesmuchene.prefeditor.preferences.favorites.FavoriteManager.Tags.APP
import com.charlesmuchene.prefeditor.preferences.favorites.FavoriteManager.Tags.DEVICE
import com.charlesmuchene.prefeditor.preferences.favorites.FavoriteManager.Tags.FILE
import org.xmlpull.v1.XmlPullParser

class FavoriteManager {

    fun read(parser: XmlPullParser, favorites: MutableList<Favorite>) {
        when (parser.name) {
            DEVICE -> favorites.add(parser.parseDevice())
            FILE -> favorites.add(parser.parseFile())
            APP -> favorites.add(parser.parseApp())
            else -> parser.skip()
        }
    }

    private fun XmlPullParser.parseDevice(): Device = gobbleTag(DEVICE) {
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