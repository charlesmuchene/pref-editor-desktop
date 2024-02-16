package com.charlesmuchene.prefeditor.screens.preferences.device

import com.charlesmuchene.prefeditor.TestFixtures.PREFERENCES
import com.charlesmuchene.prefeditor.TestFixtures.prefs
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
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
    fun decodePreferences() = runTest {
        val preferences = codec.decode(PREFERENCES).preferences
        assertEquals(expected = 8, actual = preferences.size)

        decodeSet(preferences[6])
        decodeLong(preferences[7])
        decodeFloat(preferences[4])
        decodeBoolean(preferences[0])
        decodeString(preferences[1], preferences[5])
        decodeInt(preferences[2], preferences[3])
    }

    @Test
    fun encodePreferences() = runTest {
        val preferences = prefs.preferences
        val edits = preferences.mapIndexed { index, preference ->
            val state = if (index.mod(2) == 0) PreferenceState.Deleted else PreferenceState.New
            UIPreference(preference = preference, state = state)
        }

        val output = codec.encode(edits, preferences)

        output.filterIndexed { index, _ -> index % 2 == 0 }.all { it is Edit.Delete }
    }

    @Test
    fun `cannot edit a preference that does not exist`() {
        val edits = listOf(UIPreference(IntPreference("non-existent", "1"), PreferenceState.Changed))

        assertThrows<IllegalStateException> {
            codec.encode(edits = edits, existing = prefs.preferences)
        }
    }

    @Test
    fun `cannot edit a preference that did not change value`() {
        val first = prefs.preferences.first()
        val pref = UIPreference(first, PreferenceState.Changed)
        assertThrows<IllegalArgumentException> {
            codec.encode(listOf(element = pref), prefs.preferences)
        }
    }

    private fun decodeBoolean(preference: Preference) {
        assertTrue(preference is BooleanPreference)
        assertFalse(preference.value.toBooleanStrict())
        assertEquals(expected = "boolean", actual = preference.name)
    }

    private fun decodeString(preference: Preference, another: Preference) {
        assertTrue(preference is StringPreference)
        assertEquals(expected = "string", actual = preference.name)
        assertEquals(expected = "string", actual = preference.value)
        assertTrue(another is StringPreference)
        assertEquals(expected = "", actual = another.value)
        assertEquals(expected = "empty-string", actual = another.name)
    }

    private fun decodeInt(preference: Preference, another: Preference) {
        assertTrue(preference is IntPreference)
        assertEquals(expected = -1, actual = preference.value.toInt())
        assertEquals(expected = "integer", actual = preference.name)

        assertTrue(another is IntPreference)
        assertEquals(expected = 0, actual = another.value.toInt())
        assertEquals(expected = "another-integer", actual = another.name)
    }

    private fun decodeFloat(preference: Preference) {
        assertTrue(preference is FloatPreference)
        assertEquals(expected = "float", actual = preference.name)
        assertEquals(expected = 0.0f, actual = preference.value.toFloat())
    }

    private fun decodeLong(preference: Preference) {
        assertTrue(preference is LongPreference)
        assertEquals(expected = 100L, actual = preference.value.toLong())
        assertEquals(expected = "long", actual = preference.name)
    }

    private fun decodeSet(preference: Preference) {
        assertTrue(preference is SetPreference)
        assertEquals(expected = "string-set", actual = preference.name)
        val subPreferences = preference.entries
        assertEquals(expected = 4, actual = subPreferences.size)
        assertEquals(expected = "strings", actual = subPreferences.first())
        assertEquals(expected = "one", actual = subPreferences[1])
        assertEquals(expected = "two", actual = subPreferences[2])
        assertEquals(expected = "three", actual = subPreferences[3])
    }
}