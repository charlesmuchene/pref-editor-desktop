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
    fun `parse boolean entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries.first()
            assertTrue(entry is BooleanEntry)
            assertFalse(entry.value.toBooleanStrict())
            assertEquals(expected = "boolean", actual = entry.name)
        }
    }

    @Test
    fun `parse string entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[1]
            assertTrue(entry is StringEntry)
            assertEquals(expected = "string", actual = entry.name)
            assertEquals(expected = "string", actual = entry.value)
        }
    }

    @Test
    fun `parse int entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entryOne = entries[2]
            val entryTwo = entries[3]
            assertTrue(entryOne is IntEntry)
            assertTrue(entryTwo is IntEntry)
            assertEquals(expected = 0, actual = entryOne.value.toInt())
            assertEquals(expected = "another-integer", actual = entryOne.name)
            assertEquals(expected = -1, actual = entryTwo.value.toInt())
            assertEquals(expected = "integer", actual = entryTwo.name)
        }
    }

    @Test
    fun `parse float entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[4]
            assertTrue(entry is FloatEntry)
            assertEquals(expected = 0.0f, actual = entry.value.toFloat())
            assertEquals(expected = "float", actual = entry.name)
        }
    }

    @Test
    fun `parse long entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[6]
            assertTrue(entry is LongEntry)
            assertEquals(expected = 100L, actual = entry.value.toLong())
            assertEquals(expected = "long", actual = entry.name)
        }
    }

    @Test
    fun `parse set entry`() {
        TestFixtures.preferenceFile.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[5]
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
}