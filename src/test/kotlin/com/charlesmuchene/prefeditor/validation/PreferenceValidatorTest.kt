package com.charlesmuchene.prefeditor.validation

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.IntEntry
import com.charlesmuchene.prefeditor.data.StringEntry
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
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
}