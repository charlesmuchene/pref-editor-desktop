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

import com.charlesmuchene.prefeditor.data.Tags
import kotlinx.coroutines.yield
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.io.OutputStream

/**
 * Manages encoding and decoding preferences
 */
class PreferencesCodec : PreferenceDecoder, PreferenceEncoder {

    override suspend fun decode(inputStream: InputStream, block: XmlPullParser.() -> Unit) {
        with(XmlPullParserFactory.newInstance().newPullParser()) {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(BufferedInputStream(inputStream), null)
            nextTag()
            require(XmlPullParser.START_TAG, null, Tags.ROOT)
            while (nextTag() != XmlPullParser.END_TAG) {
                yield()
                if (eventType != XmlPullParser.START_TAG) continue
                block()
            }
        }
    }

    override fun encode(block: XmlSerializer.() -> Unit): String = ByteArrayOutputStream().run {
        encode(outputStream = this, block = block)
        toString().trim()
    }

    override fun encode(outputStream: OutputStream, block: XmlSerializer.() -> Unit) {
        with(XmlPullParserFactory.newInstance().newSerializer()) {
            setFeature(INDENTATION_FEATURE, true)
            setOutput(outputStream, ENCODING)
            block()
            flush()
        }
    }

    override fun encodeDocument(block: XmlSerializer.() -> Unit): String = encode {
        startDocument(ENCODING, true)
        startTag(null, Tags.ROOT)
        block()
        if (depth == 1) endTag(null, Tags.ROOT)
        endDocument()
    }

    companion object Manager {
        const val INDENTATION_FEATURE = "http://xmlpull.org/v1/doc/features.html#indent-output"
        const val ENCODING = "utf-8"
    }
}