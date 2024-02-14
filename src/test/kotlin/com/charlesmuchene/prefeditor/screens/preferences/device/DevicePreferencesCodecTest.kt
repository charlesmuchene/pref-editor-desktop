package com.charlesmuchene.prefeditor.screens.preferences.device

import com.charlesmuchene.prefeditor.TestFixtures.PREFERENCES
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class DevicePreferencesCodecTest {
    private lateinit var codec: DevicePreferencesCodec

    @BeforeEach
    fun setup() {
        codec = DevicePreferencesCodec(PreferencesCodec())
    }

    @Test
    fun `parse all preferences`() = runTest {
            val preferences = codec.decode(PREFERENCES).preferences
            kotlin.test.assertEquals(expected = 8, actual = preferences.size)

            `parse set preference`(preferences[6])
            `parse long preference`(preferences[7])
            `parse float preference`(preferences[4])
            `parse boolean preference`(preferences[0])
            `parse string preferences`(preferences[1], preferences[5])
            `parse int preferences`(preferences[2], preferences[3])
    }

    private fun `parse boolean preference`(preference: Preference) {
        kotlin.test.assertTrue(preference is BooleanPreference)
        kotlin.test.assertFalse(preference.value.toBooleanStrict())
        kotlin.test.assertEquals(expected = "boolean", actual = preference.name)
    }

    private fun `parse string preferences`(preference: Preference, another: Preference) {
        kotlin.test.assertTrue(preference is StringPreference)
        kotlin.test.assertEquals(expected = "string", actual = preference.name)
        kotlin.test.assertEquals(expected = "string", actual = preference.value)
        kotlin.test.assertTrue(another is StringPreference)
        kotlin.test.assertEquals(expected = "", actual = another.value)
        kotlin.test.assertEquals(expected = "empty-string", actual = another.name)
    }

    private fun `parse int preferences`(preference: Preference, another: Preference) {
        kotlin.test.assertTrue(preference is IntPreference)
        kotlin.test.assertEquals(expected = -1, actual = preference.value.toInt())
        kotlin.test.assertEquals(expected = "integer", actual = preference.name)

        kotlin.test.assertTrue(another is IntPreference)
        kotlin.test.assertEquals(expected = 0, actual = another.value.toInt())
        kotlin.test.assertEquals(expected = "another-integer", actual = another.name)
    }

    private fun `parse float preference`(preference: Preference) {
        kotlin.test.assertTrue(preference is FloatPreference)
        kotlin.test.assertEquals(expected = "float", actual = preference.name)
        kotlin.test.assertEquals(expected = 0.0f, actual = preference.value.toFloat())
    }

    private fun `parse long preference`(preference: Preference) {
        kotlin.test.assertTrue(preference is LongPreference)
        kotlin.test.assertEquals(expected = 100L, actual = preference.value.toLong())
        kotlin.test.assertEquals(expected = "long", actual = preference.name)
    }

    private fun `parse set preference`(preference: Preference) {
        kotlin.test.assertTrue(preference is SetPreference)
        kotlin.test.assertEquals(expected = "string-set", actual = preference.name)
        val subPreferences = preference.entries
        kotlin.test.assertEquals(expected = 4, actual = subPreferences.size)
        kotlin.test.assertEquals(expected = "strings", actual = subPreferences.first())
        kotlin.test.assertEquals(expected = "one", actual = subPreferences[1])
        kotlin.test.assertEquals(expected = "two", actual = subPreferences[2])
        kotlin.test.assertEquals(expected = "three", actual = subPreferences[3])
    }
}