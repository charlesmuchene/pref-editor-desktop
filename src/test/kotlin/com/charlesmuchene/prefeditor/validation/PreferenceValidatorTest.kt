package com.charlesmuchene.prefeditor.validation

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.FloatPreference
import com.charlesmuchene.prefeditor.data.IntPreference
import com.charlesmuchene.prefeditor.data.LongPreference
import com.charlesmuchene.prefeditor.data.StringPreference
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferenceValidatorTest {

    private lateinit var validator: PreferenceValidator

    @BeforeEach
    fun setup() {
        validator = PreferenceValidator(TestFixtures.prefs.preferences)
    }

    @Test
    fun `valid edits`() {
        val edits = mapOf("another-integer" to (IntPreference::class to "4"))

        val isValid = validator.allowedEdits(edits)

        assertTrue(isValid)
    }

    @Test
    fun `a string edit is always valid`() {
        val edits = mapOf("string" to (StringPreference::class to ""))

        val isValid = validator.allowedEdits(edits)

        assertTrue(isValid)
    }

    @Test
    fun `an empty integer value is invalid`() {
        val preference = IntPreference(name = "empty-integer", value = "")

        val isValid = validator.isValid(preference)

        assertFalse(isValid)
    }

    @Test
    fun `a very long number is invalid`() {
        val preference = LongPreference(name = "cannot-be-a-long", value = "789456123065403210987")

        val isValid = validator.isValid(preference)

        assertFalse(isValid)
    }

    @Test
    fun `a float number is valid`() {
        val preference = FloatPreference(name = "valid-float", value = "3.142678324")

        val isValid = validator.isValid(preference)

        assertFalse(isValid)
    }
}