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

package com.charlesmuchene.prefeditor.screens.preferences.device.codec

import com.charlesmuchene.prefeditor.data.BooleanPreference
import com.charlesmuchene.prefeditor.data.FloatPreference
import com.charlesmuchene.prefeditor.data.IntPreference
import com.charlesmuchene.prefeditor.data.LongPreference
import com.charlesmuchene.prefeditor.data.Preference
import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.data.SetPreference
import com.charlesmuchene.prefeditor.data.StringPreference
import com.charlesmuchene.prefeditor.data.Tags
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.skip
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

class DevicePreferencesDecoderImpl(private val decoder: PreferenceDecoder) : DevicePreferencesDecoder {
    override suspend fun decode(content: String): Preferences {
        val preferences =
            buildList {
                // TODO Handle xml pull parser exception
                decoder.decode(content.byteInputStream()) {
                    when (name) {
                        Tags.BOOLEAN -> add(parseBoolean())
                        Tags.STRING -> add(parseString())
                        Tags.FLOAT -> add(parseFloat())
                        Tags.LONG -> add(parseLong())
                        Tags.INT -> add(parseInt())
                        Tags.SET -> add(parseSet())
                        else -> skip()
                    }
                }
            }
        return Preferences(preferences = preferences)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseBoolean(): BooleanPreference =
        parse(tag = Tags.BOOLEAN) { name, value ->
            BooleanPreference(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseInt(): IntPreference =
        parse(tag = Tags.INT) { name, value ->
            IntPreference(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseFloat(): FloatPreference =
        parse(tag = Tags.FLOAT) { name, value ->
            FloatPreference(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseLong(): LongPreference =
        parse(tag = Tags.LONG) { name, value ->
            LongPreference(name = name, value = value)
        }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseString(): StringPreference {
        require(XmlPullParser.START_TAG, null, Tags.STRING)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val next = next()
        val value = if (next == XmlPullParser.TEXT) text else ""
        if (next != XmlPullParser.END_TAG) nextTag()
        require(XmlPullParser.END_TAG, null, Tags.STRING)
        return StringPreference(name = name, value = value)
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseSet(): SetPreference {
        require(XmlPullParser.START_TAG, null, Tags.SET)
        expect(attributeCount == 1)
        val name = getAttributeValue(0)
        val entries =
            buildList {
                while (nextTag() != XmlPullParser.END_TAG) {
                    add(parseNamelessString())
                }
            }
        val preference = SetPreference(name = name, entries = entries)
        require(XmlPullParser.END_TAG, null, Tags.SET)
        return preference
    }

    @Throws(XmlPullParserException::class)
    private fun XmlPullParser.parseNamelessString(): String =
        gobbleTag(tag = Tags.STRING) {
            expect(attributeCount == 0)
            expect(next() == XmlPullParser.TEXT)
            text
        }

    @Throws(XmlPullParserException::class)
    private fun <R : Preference> XmlPullParser.parse(
        tag: String,
        block: (String, String) -> R,
    ): R =
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
