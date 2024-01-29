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

package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.data.Tags.BOOLEAN
import com.charlesmuchene.prefeditor.data.Tags.FLOAT
import com.charlesmuchene.prefeditor.data.Tags.INT
import com.charlesmuchene.prefeditor.data.Tags.LONG
import com.charlesmuchene.prefeditor.data.Tags.ROOT
import com.charlesmuchene.prefeditor.data.Tags.SET
import com.charlesmuchene.prefeditor.data.Tags.STRING
import com.charlesmuchene.prefeditor.preferences.PreferenceManager
import com.charlesmuchene.prefeditor.preferences.PreferenceReader
import com.charlesmuchene.prefeditor.preferences.PreferenceReader.Reader.skip
import okio.BufferedSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class PrefParser(private val reader: PreferenceReader = PreferenceManager()) : Parser<Preferences> {

    override fun parse(source: BufferedSource): Preferences {
        val entries = buildList {
            reader.read(source.inputStream()) {
                when (name) {
                    BOOLEAN -> add(parseBoolean())
                    STRING -> add(parseString())
                    FLOAT -> add(parseFloat())
                    LONG -> add(parseLong())
                    INT -> add(parseInt())
                    SET -> add(parseSet())
                    else -> skip()
                }
            }
        }
        return Preferences(entries = entries)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseBoolean(): BooleanEntry =
        parse(tag = BOOLEAN) { name, value ->
            BooleanEntry(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseInt(): IntEntry = parse(tag = INT) { name, value ->
        IntEntry(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseFloat(): FloatEntry = parse(tag = FLOAT) { name, value ->
        FloatEntry(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseLong(): LongEntry = parse(tag = LONG) { name, value ->
        LongEntry(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseString(): StringEntry {
        require(XmlPullParser.START_TAG, null, STRING)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val next = next()
        val value = if (next == XmlPullParser.TEXT) text else ""
        if (next != XmlPullParser.END_TAG) nextTag()
        require(XmlPullParser.END_TAG, null, STRING)
        return StringEntry(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseSet(): SetEntry {
        require(XmlPullParser.START_TAG, null, SET)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val entries = buildList {
            while (nextTag() != XmlPullParser.END_TAG) {
                add(parseNamelessString())
            }
        }
        val entry = SetEntry(name = name, entries = entries)
        require(XmlPullParser.END_TAG, null, SET)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseNamelessString(): String = gobbleTag(tag = STRING) {
        expect(attributeCount == 0)
        expect(next() == XmlPullParser.TEXT)
        text
    }

    @Throws(XmlPullParserException::class)
    private fun <R : Entry> XmlPullParser.parse(tag: String, block: (String, String) -> R): R =
        gobbleTag(tag) {
            expect(attributeCount == 2)
            val name = getAttributeValue(0)
            val value = getAttributeValue(1)
            block(name, value)
        }

    private fun expect(toBeTrue: Boolean) {
        if (!toBeTrue) throw XmlPullParserException("Unexpected xml format")
    }

    private fun <R> XmlPullParser.gobbleTag(tag: String, block: XmlPullParser.() -> R): R {
        require(XmlPullParser.START_TAG, null, tag)
        val result = block()
        nextTag()
        require(XmlPullParser.END_TAG, null, tag)
        return result
    }
}