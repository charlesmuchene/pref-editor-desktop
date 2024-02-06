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
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import org.xmlpull.v1.XmlSerializer

/**
 * Codec for preferences on device
 *
 * @param codec [PreferencesCodec] instance
 */
class DevicePreferencesCodec(private val codec: PreferencesCodec) {

    fun encode(edits: List<UIEntry>, existing: List<Entry>): List<Edit> = edits.map { entry ->
        when (entry.state) {
            EntryState.Changed -> encodeChange(
                change = entry.entry,
                existing = existing.find { it.name == entry.entry.name }
                    ?: error("${entry.entry} missing from disk entries"),
            )

            EntryState.Deleted -> encodeDelete(entry = entry.entry)
            EntryState.None -> error("Unnecessary encode: no change to $entry")
        }
    }

    /**
     * Encode an entry to be changed
     *
     * @param change [Entry] to change
     * @param existing [Entry] on disk
     * @return [Edit.Change] instance
     */
    private fun encodeChange(change: Entry, existing: Entry): Edit.Change {
        val content = codec.encode { serializeEntry(entry = change) }
        val matcher = codec.encode { serializeEntry(entry = existing) }

        return Edit.Change(content = content, matcher = matcher)
    }

    /**
     * Encode an entry to be deleted
     *
     * @param entry [Entry] to delete
     * @return [Edit.Delete] instance
     */
    private fun encodeDelete(entry: Entry): Edit.Delete {
        val matcher = codec.encode { serializeEntry(entry = entry) }
        return Edit.Delete(matcher = matcher)
    }

    /**
     * Serialize an entry
     *
     * @param entry [Entry] to serialize
     * @receiver [XmlSerializer] instance
     */
    private fun XmlSerializer.serializeEntry(entry: Entry) {
        when (entry) {
            is BooleanEntry -> tag(BOOLEAN) { attribute(name = entry.name, value = entry.value) }
            is FloatEntry -> tag(FLOAT) { attribute(name = entry.name, value = entry.value) }
            is LongEntry -> tag(LONG) { attribute(name = entry.name, value = entry.value) }
            is IntEntry -> tag(INT) { attribute(name = entry.name, value = entry.value) }

            is StringEntry -> tag(STRING) {
                attrib(name = NAME, value = entry.name)
                text(entry.value)
            }

            is SetEntry -> tag(SET) {
                attrib(name = NAME, value = entry.name)
                entry.entries.forEach { string ->
                    tag(STRING) { text(string) }
                }
            }
        }
    }

    /**
     * Create an attribute.
     *
     * Most xml entries in android have a name and value attributes
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

    companion object {
        const val VALUE = "value"
        const val NAME = "name"
    }
}