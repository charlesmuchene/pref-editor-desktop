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

package com.charlesmuchene.prefeditor.screens.preferences.codec

import org.xmlpull.v1.XmlSerializer
import java.io.OutputStream

/**
 * Contract for encoding preferences in xml format
 */
interface PreferenceEncoder {
    /**
     * Encodes content without the xml declaration to the given output stream.
     *
     * NOTE: It is your responsibility to close this stream.
     *
     * @param outputStream [OutputStream] to encode to
     * @param block Block used to add content
     */
    fun encode(
        outputStream: OutputStream,
        block: XmlSerializer.() -> Unit,
    )

    /**
     * Encodes content without the xml declaration
     *
     * Note: The library used employs \r\n as the new line character
     *
     * @param block Block used to add content
     * @return Xml output
     */
    fun encode(block: XmlSerializer.() -> Unit): String

    /**
     * Encode a xml document with the declaration
     *
     * Note: The library used employs \r\n as the new line character
     *
     * @param block Block to add content with [XmlSerializer] as the receiver
     * @return Xml output
     */
    fun encodeDocument(block: XmlSerializer.() -> Unit = {}): String

    companion object Encoder {
        /**
         * Add a tag to the current encoding context.
         * The tag will be self-closing if no content is added using the given block.
         *
         * @param tag Tag to add
         * @param block Block to add tag content with [XmlSerializer] as the receiver
         * @receiver [XmlSerializer] instance
         */
        fun XmlSerializer.tag(
            tag: String,
            block: XmlSerializer.() -> Unit,
        ) {
            startTag(null, tag)
            block()
            endTag(null, tag)
        }

        /**
         * Add an attribute to the current encoding context
         *
         * @param name Attribute name
         * @param value Attribute value
         * @receiver [XmlSerializer] instance
         */
        fun XmlSerializer.attrib(
            name: String,
            value: String,
        ) {
            attribute(null, name, value)
        }
    }
}
