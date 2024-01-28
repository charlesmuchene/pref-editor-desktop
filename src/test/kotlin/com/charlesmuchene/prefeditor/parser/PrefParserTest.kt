package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.utils.buffered
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
    fun `parse all preferences`() {
        TestFixtures.PREFERENCES.buffered {
            val entries = parser.parse(this).entries
            assertEquals(expected = 8, actual = entries.size)

            `parse set entry`(entries[6])
            `parse long entry`(entries[7])
            `parse float entry`(entries[4])
            `parse boolean entry`(entries[0])
            `parse string entries`(entries[1], entries[5])
            `parse int entries`(entries[2], entries[3])
        }
    }

    private fun `parse boolean entry`(entry: Entry) {
        assertTrue(entry is BooleanEntry)
        assertFalse(entry.value.toBooleanStrict())
        assertEquals(expected = "boolean", actual = entry.name)
    }

    private fun `parse string entries`(entry: Entry, another: Entry) {
        assertTrue(entry is StringEntry)
        assertEquals(expected = "string", actual = entry.name)
        assertEquals(expected = "string", actual = entry.value)
        assertTrue(another is StringEntry)
        assertEquals(expected = "", actual = another.value)
        assertEquals(expected = "empty-string", actual = another.name)
    }

    private fun `parse int entries`(entry: Entry, another: Entry) {
        assertTrue(entry is IntEntry)
        assertEquals(expected = -1, actual = entry.value.toInt())
        assertEquals(expected = "integer", actual = entry.name)

        assertTrue(another is IntEntry)
        assertEquals(expected = 0, actual = another.value.toInt())
        assertEquals(expected = "another-integer", actual = another.name)
    }

    private fun `parse float entry`(entry: Entry) {
        assertTrue(entry is FloatEntry)
        assertEquals(expected = "float", actual = entry.name)
        assertEquals(expected = 0.0f, actual = entry.value.toFloat())
    }

    private fun `parse long entry`(entry: Entry) {
        assertTrue(entry is LongEntry)
        assertEquals(expected = 100L, actual = entry.value.toLong())
        assertEquals(expected = "long", actual = entry.name)
    }

    private fun `parse set entry`(entry: Entry) {
        assertTrue(entry is SetEntry)
        assertEquals(expected = "string-set", actual = entry.name)
        val subEntries = entry.entries
        assertEquals(expected = 4, actual = subEntries.size)
        assertEquals(expected = "strings", actual = subEntries.first())
        assertEquals(expected = "one", actual = subEntries[1])
        assertEquals(expected = "two", actual = subEntries[2])
        assertEquals(expected = "three", actual = subEntries[3])
    }
}