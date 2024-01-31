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

import com.charlesmuchene.prefeditor.data.Tags
import okio.Buffer
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.InputStream

/**
 * Manages reading and writing preferences
 */
class PreferenceManager : PreferenceReader, PreferenceWriter {

    override fun read(inputStream: InputStream, block: XmlPullParser.() -> Unit) {
        with(XmlPullParserFactory.newInstance().newPullParser()) {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(inputStream, null)
            nextTag()
            require(XmlPullParser.START_TAG, null, Tags.ROOT)
            while (nextTag() != XmlPullParser.END_TAG) {
                if (eventType != XmlPullParser.START_TAG) continue
                block()
            }
        }
    }

    override fun write(block: XmlSerializer.() -> Unit): String = Buffer().run {
        with(XmlPullParserFactory.newInstance().newSerializer()) {
            setFeature(INDENTATION_FEATURE, true)
            setOutput(buffer.outputStream(), ENCODING)
            block()
            flush()
        }
        readUtf8().trim()
    }

    override fun writeDocument(block: XmlSerializer.() -> Unit): String = write {
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