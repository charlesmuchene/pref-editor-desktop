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

package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.data.Tags.BOOLEAN
import com.charlesmuchene.prefedit.data.Tags.FLOAT
import com.charlesmuchene.prefedit.data.Tags.INT
import com.charlesmuchene.prefedit.data.Tags.LONG
import com.charlesmuchene.prefedit.data.Tags.ROOT
import com.charlesmuchene.prefedit.data.Tags.SET
import com.charlesmuchene.prefedit.data.Tags.STRING
import okio.BufferedSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory

class PrefParser : Parser<Preferences> {
    override fun parse(source: BufferedSource): Preferences {
        val parser = XmlPullParserFactory.newInstance().newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            setInput(source.inputStream(), null)
            nextTag()
        }
        return Preferences(entries = parse(parser))
    }

    private fun parse(parser: XmlPullParser): List<Entry> = buildList {
        parser.require(XmlPullParser.START_TAG, null, ROOT)
        while (parser.nextTag() != XmlPullParser.END_TAG) {
            if (parser.eventType != XmlPullParser.START_TAG) continue
            when (parser.name) {
                BOOLEAN -> add(parseBoolean(parser))
                STRING -> add(parseString(parser))
                FLOAT -> add(parseFloat(parser))
                LONG -> add(parseLong(parser))
                INT -> add(parseInt(parser))
                SET -> add(parseSet(parser))
                else -> skip(parser)
            }
        }
    }

    @Throws(XmlPullParserException::class)
    private fun parseBoolean(parser: XmlPullParser): BooleanEntry =
        parse(tag = BOOLEAN, parser = parser) { name, value ->
            BooleanEntry(name = name, value = value.toBooleanStrict())
        }

    @Throws(XmlPullParserException::class)
    private fun parseInt(parser: XmlPullParser): IntEntry = parse(tag = INT, parser = parser) { name, value ->
        IntEntry(name = name, value = value.toInt())
    }

    @Throws(XmlPullParserException::class)
    private fun parseFloat(parser: XmlPullParser): FloatEntry = parse(tag = FLOAT, parser = parser) { name, value ->
        FloatEntry(name = name, value = value.toFloat())
    }

    @Throws(XmlPullParserException::class)
    private fun parseLong(parser: XmlPullParser): LongEntry = parse(tag = LONG, parser = parser) { name, value ->
        LongEntry(name = name, value = value.toLong())
    }

    @Throws(XmlPullParserException::class)
    private fun parseString(parser: XmlPullParser): StringEntry = parser.gobbleTag(tag = STRING) {
        expect(parser.attributeCount == 1)
        val name = parser.getAttributeValue(0)
        expect(parser.next() == XmlPullParser.TEXT)
        StringEntry(name = name, value = parser.text)
    }

    @Throws(XmlPullParserException::class)
    private fun parseSet(parser: XmlPullParser): SetEntry {
        parser.require(XmlPullParser.START_TAG, null, SET)
        expect(parser.attributeCount == 1)
        val name = parser.getAttributeValue(0)
        val entries = buildList {
            while (parser.nextTag() != XmlPullParser.END_TAG) {
                add(parseNamelessString(parser))
            }
        }
        val entry = SetEntry(name = name, entries = entries)
        parser.require(XmlPullParser.END_TAG, null, SET)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun parseNamelessString(parser: XmlPullParser): String = parser.gobbleTag(tag = STRING) {
        expect(parser.attributeCount == 0)
        expect(parser.next() == XmlPullParser.TEXT)
        parser.text
    }

    @Throws(XmlPullParserException::class)
    private fun <R : Entry> parse(tag: String, parser: XmlPullParser, block: (String, String) -> R): R =
        parser.gobbleTag(tag) {
            expect(parser.attributeCount == 2)
            val name = parser.getAttributeValue(0)
            val value = parser.getAttributeValue(1)
            block(name, value)
        }

    @Throws(XmlPullParserException::class)
    private fun skip(parser: XmlPullParser) {
        expect(parser.eventType == XmlPullParser.START_TAG)
        var depth = 1
        while (depth != 0) {
            when (parser.next()) {
                XmlPullParser.END_TAG -> depth--
                XmlPullParser.START_TAG -> depth++
            }
        }
    }

    private fun expect(toBeTrue: Boolean) {
        if (!toBeTrue) throw XmlPullParserException("Failed xml format check")
    }

    private fun <R> XmlPullParser.gobbleTag(tag: String, block: XmlPullParser.() -> R): R {
        require(XmlPullParser.START_TAG, null, tag)
        val result = block()
        nextTag()
        require(XmlPullParser.END_TAG, null, tag)
        return result
    }
}