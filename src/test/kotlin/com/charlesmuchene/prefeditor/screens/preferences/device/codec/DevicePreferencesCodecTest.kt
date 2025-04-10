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
import com.charlesmuchene.prefeditor.TestFixtures.PREFERENCES
import com.charlesmuchene.prefeditor.TestFixtures.prefs
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class DevicePreferencesCodecTest {
    private lateinit var codec: DevicePreferencesCodec

    @BeforeEach
    fun setup() {
        codec = DevicePreferencesCodec(PreferencesCodec())
    }

    @Test
    fun decodePreferences() =
        runTest {
            val preferences =
                (codec.decode(PREFERENCES.toByteArray(), PrefFile.Type.KEY_VALUE) as KeyValuePreferences).preferences
            assertEquals(expected = 8, actual = preferences.size)

            decodeSet(preferences[6])
            decodeLong(preferences[7])
            decodeFloat(preferences[4])
            decodeBoolean(preferences[0])
            decodeString(preferences[1], preferences[5])
            decodeInt(preferences[2], preferences[3])
        }

    @Test
    fun encodePreferences() =
        runTest {
            val preferences = prefs.preferences
            val edits =
                preferences.mapIndexed { index, preference ->
                    val state = if (index.mod(2) == 0) PreferenceState.Deleted else PreferenceState.New
                    UIPreference(preference = preference, state = state)
                }

            val output = codec.encode(edits, prefs)

            output.filterIndexed { index, _ -> index % 2 == 0 }.all { it is Edit.Delete }
        }

    @Test
    fun `cannot edit a preference that does not exist`() {
        val edits = listOf(UIPreference(IntPreference("non-existent", "1"), PreferenceState.Changed))

        assertThrows<IllegalStateException> {
            codec.encode(edits = edits, existing = prefs)
        }
    }

    @Test
    fun `cannot edit a preference that did not change value`() {
        val first = prefs.preferences.first()
        val pref = UIPreference(first, PreferenceState.Changed)
        assertThrows<IllegalArgumentException> {
            codec.encode(listOf(element = pref), prefs)
        }
    }

    private fun decodeBoolean(preference: Preference) {
        assertTrue(preference is BooleanPreference)
        assertFalse(preference.value.toBooleanStrict())
        assertEquals(expected = "boolean", actual = preference.key)
    }

    private fun decodeString(
        preference: Preference,
        another: Preference,
    ) {
        assertTrue(preference is StringPreference)
        assertEquals(expected = "string", actual = preference.key)
        assertEquals(expected = "string", actual = preference.value)
        assertTrue(another is StringPreference)
        assertEquals(expected = "", actual = another.value)
        assertEquals(expected = "empty-string", actual = another.key)
    }

    private fun decodeInt(
        preference: Preference,
        another: Preference,
    ) {
        assertTrue(preference is IntPreference)
        assertEquals(expected = -1, actual = preference.value.toInt())
        assertEquals(expected = "integer", actual = preference.key)

        assertTrue(another is IntPreference)
        assertEquals(expected = 0, actual = another.value.toInt())
        assertEquals(expected = "another-integer", actual = another.key)
    }

    private fun decodeFloat(preference: Preference) {
        assertTrue(preference is FloatPreference)
        assertEquals(expected = "float", actual = preference.key)
        assertEquals(expected = 0.0f, actual = preference.value.toFloat())
    }

    private fun decodeLong(preference: Preference) {
        assertTrue(preference is LongPreference)
        assertEquals(expected = 100L, actual = preference.value.toLong())
        assertEquals(expected = "long", actual = preference.key)
    }

    private fun decodeSet(preference: Preference) {
        assertTrue(preference is StringSetPreference)
        assertEquals(expected = "string-set", actual = preference.key)
        val subPreferences = preference.entries
        assertEquals(expected = 4, actual = subPreferences.size)
        assertTrue(subPreferences.contains("strings"))
        assertTrue(subPreferences.contains("one"))
        assertTrue(subPreferences.contains("two"))
        assertTrue(subPreferences.contains("three"))
    }
}
