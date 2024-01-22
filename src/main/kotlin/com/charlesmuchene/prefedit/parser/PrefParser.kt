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
    private fun parseBoolean(parser: XmlPullParser): BooleanEntry {
        parser.require(XmlPullParser.START_TAG, null, BOOLEAN)
        expect(parser.attributeCount == 2)
        val name = parser.getAttributeValue(0)
        val value = parser.getAttributeValue(1).toBooleanStrict()
        val entry = BooleanEntry(name = name, value = value)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, BOOLEAN)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun parseString(parser: XmlPullParser): StringEntry {
        parser.require(XmlPullParser.START_TAG, null, STRING)
        expect(parser.attributeCount == 1)
        val name = parser.getAttributeValue(0)
        expect(parser.next() == XmlPullParser.TEXT)
        val value = parser.text
        val entry = StringEntry(name = name, value = value)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, STRING)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun parseInt(parser: XmlPullParser): IntEntry {
        parser.require(XmlPullParser.START_TAG, null, INT)
        expect(parser.attributeCount == 2)
        val name = parser.getAttributeValue(0)
        val value = parser.getAttributeValue(1).toInt()
        val entry = IntEntry(name = name, value = value)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, INT)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun parseFloat(parser: XmlPullParser): FloatEntry {
        parser.require(XmlPullParser.START_TAG, null, FLOAT)
        expect(parser.attributeCount == 2)
        val name = parser.getAttributeValue(0)
        val value = parser.getAttributeValue(1).toFloat()
        val entry = FloatEntry(name = name, value = value)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, FLOAT)
        return entry
    }

    @Throws(XmlPullParserException::class)
    private fun parseLong(parser: XmlPullParser): LongEntry {
        parser.require(XmlPullParser.START_TAG, null, LONG)
        expect(parser.attributeCount == 2)
        val name = parser.getAttributeValue(0)
        val value = parser.getAttributeValue(1).toLong()
        val entry = LongEntry(name = name, value = value)
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, LONG)
        return entry
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
    private fun parseNamelessString(parser: XmlPullParser): String {
        parser.require(XmlPullParser.START_TAG, null, STRING)
        expect(parser.attributeCount == 0)
        expect(parser.next() == XmlPullParser.TEXT)
        val value = parser.text
        parser.nextTag()
        parser.require(XmlPullParser.END_TAG, null, STRING)
        return value
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

    private companion object Tags {
        const val ROOT = "map"
        const val SET = "set"
        const val INT = "int"
        const val LONG = "long"
        const val FLOAT = "float"
        const val STRING = "string"
        const val BOOLEAN = "boolean"
    }
}