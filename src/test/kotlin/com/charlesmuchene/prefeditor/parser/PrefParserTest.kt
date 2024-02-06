package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.utils.buffered
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PrefParserTest {

    private lateinit var parser: PrefParser

    @BeforeEach
    fun setup() {
        parser = PrefParser()
    }

    @Test
    fun `parse all preferences`() = runTest {
        TestFixtures.PREFERENCES.buffered {
            val preferences = parser.parse(this).preferences
            assertEquals(expected = 8, actual = preferences.size)

            `parse set preference`(preferences[6])
            `parse long preference`(preferences[7])
            `parse float preference`(preferences[4])
            `parse boolean preference`(preferences[0])
            `parse string preferences`(preferences[1], preferences[5])
            `parse int preferences`(preferences[2], preferences[3])
        }
    }

    private fun `parse boolean preference`(preference: Preference) {
        assertTrue(preference is BooleanPreference)
        assertFalse(preference.value.toBooleanStrict())
        assertEquals(expected = "boolean", actual = preference.name)
    }

    private fun `parse string preferences`(preference: Preference, another: Preference) {
        assertTrue(preference is StringPreference)
        assertEquals(expected = "string", actual = preference.name)
        assertEquals(expected = "string", actual = preference.value)
        assertTrue(another is StringPreference)
        assertEquals(expected = "", actual = another.value)
        assertEquals(expected = "empty-string", actual = another.name)
    }

    private fun `parse int preferences`(preference: Preference, another: Preference) {
        assertTrue(preference is IntPreference)
        assertEquals(expected = -1, actual = preference.value.toInt())
        assertEquals(expected = "integer", actual = preference.name)

        assertTrue(another is IntPreference)
        assertEquals(expected = 0, actual = another.value.toInt())
        assertEquals(expected = "another-integer", actual = another.name)
    }

    private fun `parse float preference`(preference: Preference) {
        assertTrue(preference is FloatPreference)
        assertEquals(expected = "float", actual = preference.name)
        assertEquals(expected = 0.0f, actual = preference.value.toFloat())
    }

    private fun `parse long preference`(preference: Preference) {
        assertTrue(preference is LongPreference)
        assertEquals(expected = 100L, actual = preference.value.toLong())
        assertEquals(expected = "long", actual = preference.name)
    }

    private fun `parse set preference`(preference: Preference) {
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