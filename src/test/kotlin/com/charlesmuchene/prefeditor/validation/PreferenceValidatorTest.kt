package com.charlesmuchene.prefeditor.validation

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.FloatEntry
import com.charlesmuchene.prefeditor.data.IntEntry
import com.charlesmuchene.prefeditor.data.LongEntry
import com.charlesmuchene.prefeditor.data.StringEntry
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferenceValidatorTest {

    private lateinit var validator: PreferenceValidator

    @BeforeEach
    fun setup() {
        validator = PreferenceValidator(TestFixtures.prefs.entries)
    }

    @Test
    fun `valid edits`() {
        val edits = mapOf("another-integer" to (IntEntry::class to "4"))

        val isValid = validator.validEdits(edits)

        assertTrue(isValid)
    }

    @Test
    fun `a string edit is always valid`() {
        val edits = mapOf("string" to (StringEntry::class to ""))

        val isValid = validator.validEdits(edits)

        assertTrue(isValid)
    }

    @Test
    fun `an empty integer value is invalid`() {
        val entry = IntEntry(name = "empty-integer", value = "")

        val isValid = validator.isValid(entry)

        assertFalse(isValid)
    }

    @Test
    fun `a very long number is invalid`() {
        val entry = LongEntry(name = "cannot-be-a-long", value = "789456123065403210987")

        val isValid = validator.isValid(entry)

        assertFalse(isValid)
    }

    @Test
    fun `a float number is valid`() {
        val entry = FloatEntry(name = "valid-float", value = "3.142678324")

        val isValid = validator.isValid(entry)

        assertFalse(isValid)
    }
}