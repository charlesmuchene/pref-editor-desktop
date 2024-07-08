package com.charlesmuchene.prefeditor.validation

import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.PreferenceValidator
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class PreferenceValidatorTest {
    private lateinit var validator: PreferenceValidator

    @BeforeEach
    fun setup() {
        validator = PreferenceValidator()
    }

    @Test
    fun `valid edits`() {
        val preference = IntPreference("another-integer", "4")

        val isValid = validator.isValid(preference)

        assertTrue(isValid)
    }

    @Test
    fun `a string edit is always valid`() {
        val preference = StringPreference("string", "")

        val isValid = validator.isValid(preference)

        assertTrue(isValid)
    }

    @Test
    fun `an empty integer value is invalid`() {
        val preference = IntPreference(key = "empty-integer", value = "")

        val isValid = validator.isValid(preference)

        assertFalse(isValid)
    }

    @Test
    fun `a very long number is invalid`() {
        val preference = LongPreference(key = "cannot-be-a-long", value = "789456123065403210987")

        val isValid = validator.isValid(preference)

        assertFalse(isValid)
    }

    @Test
    fun `a float number is valid`() {
        val preference = FloatPreference(key = "valid-float", value = "3.142678324")

        val isValid = validator.isValid(preference)

        assertTrue(isValid)
    }

    @Test
    fun `edit check returns false if there are no edited preferences`() {
        val edits = mapOf("this" to UIPreference(IntPreference("int", "0")))

        val hasEdits = validator.hasEdits(edits)

        assertFalse(hasEdits)
    }

    @Test
    fun `edit check returns true if there is an edited preference`() {
        val edits = mapOf("this" to UIPreference(IntPreference("int", "0"), PreferenceState.Deleted))

        val hasEdits = validator.hasEdits(edits)

        assertTrue(hasEdits)
    }
}
