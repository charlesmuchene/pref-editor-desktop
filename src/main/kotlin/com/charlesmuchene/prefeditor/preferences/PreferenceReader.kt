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

import org.xmlpull.v1.XmlPullParser
import java.io.InputStream

interface PreferenceReader {

    fun read(inputStream: InputStream, block: XmlPullParser.() -> Unit)

    companion object Reader {

        fun XmlPullParser.skip() {
            require(eventType == XmlPullParser.START_TAG)
            var depth = 1
            while (depth != 0) {
                when (next()) {
                    XmlPullParser.END_TAG -> depth--
                    XmlPullParser.START_TAG -> depth++
                }
            }
        }

        fun <R> XmlPullParser.gobbleTag(tag: String, block: XmlPullParser.() -> R): R {
            require(XmlPullParser.START_TAG, null, tag)
            val result = block()
            nextTag()
            require(XmlPullParser.END_TAG, null, tag)
            return result
        }
    }
}