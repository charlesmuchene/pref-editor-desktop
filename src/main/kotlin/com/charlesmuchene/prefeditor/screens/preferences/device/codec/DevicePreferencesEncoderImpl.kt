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

import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.datastore.preferences.StringSetPreference
import com.charlesmuchene.datastore.preferences.encodePreferences
import com.charlesmuchene.prefeditor.data.DatastorePreferences
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.data.Tags
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import org.xmlpull.v1.XmlSerializer
import kotlin.io.encoding.Base64

class DevicePreferencesEncoderImpl(private val encoder: PreferenceEncoder) : DevicePreferencesEncoder {

    override fun encode(edits: List<UIPreference>, existing: Preferences): List<Edit> =
        when (val preferences = existing) {
            is DatastorePreferences -> encodeDatastorePreferences(edits = edits, existing = preferences.prefs())
            is KeyValuePreferences -> encodeKeyValuePreferences(edits = edits, existing = preferences.prefs())
        }

    private fun encodeKeyValuePreferences(edits: List<UIPreference>, existing: List<Preference>) =
        edits.map { preference ->
            when (preference.state) {
                PreferenceState.Changed -> {
                    val change = preference.preference
                    val initial = (
                            existing.find { it.key == preference.preference.key }
                                ?: error("${preference.preference} is missing from existing preferences")
                            )
                    require(value = change.value != initial.value) { "${change.key} didn't change value" }
                    encodeChange(change = change, existing = initial)
                }

                PreferenceState.New -> encodeAdd(preference = preference.preference)
                PreferenceState.Deleted -> encodeDelete(preference = preference.preference)
                PreferenceState.None -> error("Unnecessary encode: no change to $preference")
            }
        }

    private fun encodeDatastorePreferences(edits: List<UIPreference>, existing: List<Preference>): List<Edit> {
        val preferences = buildList {
            for (preference in existing) {
                val edited = edits.find { it.preference.key == preference.key }
                if (edited == null) add(preference)
                else when (edited.state) {
                    PreferenceState.Deleted -> Unit
                    PreferenceState.Changed -> add(edited.preference)
                    else -> error("Illegal encode for $preference")
                }
            }
            addAll(edits.filter { it.state == PreferenceState.New }.map(UIPreference::preference))
        }
        return listOf(Edit.Replace(content = Base64.encode(encodePreferences(preferences))))
    }

    /**
     * Encode a preference to be changed
     *
     * @param change [Preference] to change
     * @param existing [Preference] on disk
     * @return [Edit.Change] instance
     */
    private fun encodeChange(
        change: Preference,
        existing: Preference,
    ): Edit.Change {
        val content = encoder.encode { serializePreference(preference = change) }
        val matcher = encoder.encode { serializePreference(preference = existing) }

        return Edit.Change(content = content, matcher = matcher)
    }

    /**
     * Encode a preference to be deleted
     *
     * @param preference [Preference] to delete
     * @return [Edit.Delete] instance
     */
    private fun encodeDelete(preference: Preference): Edit.Delete {
        val matcher = encoder.encode { serializePreference(preference = preference) }
        return Edit.Delete(matcher = matcher)
    }

    /**
     * Encode a preference to be added
     *
     * @param preference [Preference] to add
     * @return [Edit.Add] instance
     */
    private fun encodeAdd(preference: Preference): Edit.Add {
        val content = encoder.encode { serializePreference(preference = preference) }
        return Edit.Add(content = content)
    }

    /**
     * Serialize a preference
     *
     * @param preference [Preference] to serialize
     * @receiver [XmlSerializer] instance
     */
    private fun XmlSerializer.serializePreference(preference: Preference) {
        when (preference) {
            is BooleanPreference -> tag(Tags.BOOLEAN) { attribute(name = preference.key, value = preference.value) }
            is FloatPreference -> tag(Tags.FLOAT) { attribute(name = preference.key, value = preference.value) }
            is LongPreference -> tag(Tags.LONG) { attribute(name = preference.key, value = preference.value) }
            is IntPreference -> tag(Tags.INT) { attribute(name = preference.key, value = preference.value) }

            is StringPreference ->
                tag(Tags.STRING) {
                    attrib(name = DevicePreferencesCodec.NAME, value = preference.key)
                    text(preference.value)
                }

            is StringSetPreference ->
                tag(Tags.SET) {
                    attrib(name = DevicePreferencesCodec.NAME, value = preference.key)
                    preference.entries.forEach { string ->
                        tag(Tags.STRING) { text(string) }
                    }
                }

            else -> error("$preference is not supported in key value preferences")
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
    private fun XmlSerializer.attribute(
        name: String,
        value: String,
    ) {
        attribute(null, DevicePreferencesCodec.NAME, name)
        attribute(null, DevicePreferencesCodec.VALUE, value)
    }
}
