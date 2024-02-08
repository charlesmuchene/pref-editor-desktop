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

package com.charlesmuchene.prefeditor.screens.preferences

import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import java.io.InputStream

/**
 * Contract to decode preferences in xml format
 */
interface PreferenceDecoder {

    /**
     * Read content from the given stream.
     *
     * Note: The provided stream is buffered before decoding begins.
     *
     * @param inputStream [InputStream] to decode from
     * @param block Block to extract tags from -- has a [XmlPullParser] as the receiver
     * @throws [XmlPullParserException]
     */
    suspend fun decode(inputStream: InputStream, block: XmlPullParser.() -> Unit)

    companion object Reader {

        /**
         * Skip the next tag in the decoding context
         *
         * @receiver [XmlPullParser] instance
         */
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

        /**
         * Consume the given tag. This must be the next tag in the decodeing context.
         *
         * @param tag The next tag to consume
         * @param block Block to consume content with [XmlPullParser] as the receiver
         * @return [R] The type of the consumed content
         * @receiver [XmlPullParser] instance
         */
        fun <R> XmlPullParser.gobbleTag(tag: String, block: XmlPullParser.() -> R): R {
            require(XmlPullParser.START_TAG, null, tag)
            val result = block()
            nextTag()
            require(XmlPullParser.END_TAG, null, tag)
            return result
        }
    }
}