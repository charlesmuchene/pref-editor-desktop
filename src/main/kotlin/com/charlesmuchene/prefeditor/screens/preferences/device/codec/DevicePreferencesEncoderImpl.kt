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

import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import org.xmlpull.v1.XmlSerializer

class DevicePreferencesEncoderImpl(private val encoder: PreferenceEncoder) : DevicePreferencesEncoder {

    override fun encode(edits: List<UIPreference>, existing: List<Preference>): List<Edit> = edits.map { preference ->
        when (preference.state) {
            PreferenceState.Changed -> {
                val change = preference.preference
                val initial = (existing.find { it.name == preference.preference.name }
                    ?: error("${preference.preference} is missing from existing preferences"))
                require(value = change.value != initial.value) { "${change.name} didn't change value" }
                encodeChange(change = change, existing = initial)
            }

            PreferenceState.New -> encodeAdd(preference = preference.preference)
            PreferenceState.Deleted -> encodeDelete(preference = preference.preference)
            PreferenceState.None -> error("Unnecessary encode: no change to $preference")
        }
    }

    /**
     * Encode a preference to be changed
     *
     * @param change [Preference] to change
     * @param existing [Preference] on disk
     * @return [Edit.Change] instance
     */
    private fun encodeChange(change: Preference, existing: Preference): Edit.Change {
        val content = encoder.encode { serializePreference(preference = change) }
        val matcher = encoder.encode { serializePreference(preference = existing) }

        return Edit.Change(content = content, matcher = matcher)
    }

    /**
     * Encode an preference to be deleted
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
            is BooleanPreference -> tag(Tags.BOOLEAN) { attribute(name = preference.name, value = preference.value) }
            is FloatPreference -> tag(Tags.FLOAT) { attribute(name = preference.name, value = preference.value) }
            is LongPreference -> tag(Tags.LONG) { attribute(name = preference.name, value = preference.value) }
            is IntPreference -> tag(Tags.INT) { attribute(name = preference.name, value = preference.value) }

            is StringPreference -> tag(Tags.STRING) {
                attrib(name = DevicePreferencesCodec.NAME, value = preference.name)
                text(preference.value)
            }

            is SetPreference -> tag(Tags.SET) {
                attrib(name = DevicePreferencesCodec.NAME, value = preference.name)
                preference.entries.forEach { string ->
                    tag(Tags.STRING) { text(string) }
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
        attribute(null, DevicePreferencesCodec.NAME, name)
        attribute(null, DevicePreferencesCodec.VALUE, value)
    }
}