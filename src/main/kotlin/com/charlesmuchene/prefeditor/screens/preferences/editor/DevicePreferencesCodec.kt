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

package com.charlesmuchene.prefeditor.screens.preferences.editor

import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.data.Tags.BOOLEAN
import com.charlesmuchene.prefeditor.data.Tags.FLOAT
import com.charlesmuchene.prefeditor.data.Tags.INT
import com.charlesmuchene.prefeditor.data.Tags.LONG
import com.charlesmuchene.prefeditor.data.Tags.SET
import com.charlesmuchene.prefeditor.data.Tags.STRING
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.preferences.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.editor.PreferenceState.*
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlSerializer

/**
 * Codec for preferences on device
 *
 * @param codec [PreferencesCodec] instance
 */
class DevicePreferencesCodec(private val codec: PreferencesCodec) {

    /**
     * Encode edits
     *
     * @param edits A developer's edits
     * @param existing Downstream preferences
     * @return A [List] of [Edit]s
     */
    fun encode(edits: List<UIPreference>, existing: List<Preference>): List<Edit> = edits.map { preference ->
        when (preference.state) {
            Changed -> encodeChange(
                change = preference.preference,
                existing = existing.find { it.name == preference.preference.name }
                    ?: error("${preference.preference} missing from disk preferences"),
            )

            Deleted -> encodeDelete(preference = preference.preference)
            None -> error("Unnecessary encode: no change to $preference")
        }
    }

    /**
     * Decode content
     *
     * @param content Output from downstream
     * @return [Preferences] instance
     */
    suspend fun decode(content: String): Preferences { // TODO Return Result?
        val preferences = buildList {
            // TODO Handle xml pull parser exception
            codec.decode(content.byteInputStream()) {
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

    /* ----------------- Writer ------------------- */

    /**
     * Encode a preference to be changed
     *
     * @param change [Preference] to change
     * @param existing [Preference] on disk
     * @return [Edit.Change] instance
     */
    private fun encodeChange(change: Preference, existing: Preference): Edit.Change {
        val content = codec.encode { serializePreference(preference = change) }
        val matcher = codec.encode { serializePreference(preference = existing) }

        return Edit.Change(content = content, matcher = matcher)
    }

    /**
     * Encode an preference to be deleted
     *
     * @param preference [Preference] to delete
     * @return [Edit.Delete] instance
     */
    private fun encodeDelete(preference: Preference): Edit.Delete {
        val matcher = codec.encode { serializePreference(preference = preference) }
        return Edit.Delete(matcher = matcher)
    }

    /**
     * Serialize a preference
     *
     * @param preference [Preference] to serialize
     * @receiver [XmlSerializer] instance
     */
    private fun XmlSerializer.serializePreference(preference: Preference) {
        when (preference) {
            is BooleanPreference -> tag(BOOLEAN) { attribute(name = preference.name, value = preference.value) }
            is FloatPreference -> tag(FLOAT) { attribute(name = preference.name, value = preference.value) }
            is LongPreference -> tag(LONG) { attribute(name = preference.name, value = preference.value) }
            is IntPreference -> tag(INT) { attribute(name = preference.name, value = preference.value) }

            is StringPreference -> tag(STRING) {
                attrib(name = NAME, value = preference.name)
                text(preference.value)
            }

            is SetPreference -> tag(SET) {
                attrib(name = NAME, value = preference.name)
                preference.entries.forEach { string ->
                    tag(STRING) { text(string) }
                }
            }
        }
    }

    /**
     * Create an attribute.
     *
     * Most xml preferences in device have a name and value attributes
     * to host their content e.g. int, float etc. This serialization
     * adds the content as expected.
     *
     * ```
     * <int name="another-integer" value="0" />
     * <float name="float" value="0.0" />
     * ```
     *
     * @param name Value of the name attribute :D
     * @param value Value of the value attribute :D
     * @receiver [XmlSerializer] instance
     */
    private fun XmlSerializer.attribute(name: String, value: String) {
        attribute(null, NAME, name)
        attribute(null, VALUE, value)
    }

    /* ----------------- Reader ------------------- */

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

    companion object {
        const val VALUE = "value"
        const val NAME = "name"
    }
}