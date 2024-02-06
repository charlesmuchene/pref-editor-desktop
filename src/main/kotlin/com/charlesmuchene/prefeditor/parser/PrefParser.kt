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
import com.charlesmuchene.prefeditor.data.Tags.SET
import com.charlesmuchene.prefeditor.data.Tags.STRING
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.skip
import okio.BufferedSource
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class PrefParser(private val reader: PreferenceDecoder = PreferencesCodec()) : Parser<Preferences> {

    override suspend fun parse(source: BufferedSource): Preferences {
        val preferences = buildList {
            reader.decode(source.inputStream()) {
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
        return Preferences(preferences = preferences)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseBoolean(): BooleanPreference =
        parse(tag = BOOLEAN) { name, value ->
            BooleanPreference(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseInt(): IntPreference = parse(tag = INT) { name, value ->
        IntPreference(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseFloat(): FloatPreference = parse(tag = FLOAT) { name, value ->
        FloatPreference(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseLong(): LongPreference = parse(tag = LONG) { name, value ->
        LongPreference(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseString(): StringPreference {
        require(XmlPullParser.START_TAG, null, STRING)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val next = next()
        val value = if (next == XmlPullParser.TEXT) text else ""
        if (next != XmlPullParser.END_TAG) nextTag()
        require(XmlPullParser.END_TAG, null, STRING)
        return StringPreference(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseSet(): SetPreference {
        require(XmlPullParser.START_TAG, null, SET)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val entries = buildList {
            while (nextTag() != XmlPullParser.END_TAG) {
                add(parseNamelessString())
            }
        }
        val preference = SetPreference(name = name, entries = entries)
        require(XmlPullParser.END_TAG, null, SET)
        return preference
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseNamelessString(): String = gobbleTag(tag = STRING) {
        expect(attributeCount == 0)
        expect(next() == XmlPullParser.TEXT)
        text
    }

    @Throws(XmlPullParserException::class)
    private fun <R : Preference> XmlPullParser.parse(tag: String, block: (String, String) -> R): R =
        gobbleTag(tag) {
            expect(attributeCount == 2)
            val name = getAttributeValue(0)
            val value = getAttributeValue(1)
            block(name, value)
        }

    private fun expect(toBeTrue: Boolean) {
        if (!toBeTrue) throw XmlPullParserException("Unexpected xml format")
    }
}