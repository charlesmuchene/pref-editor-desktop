package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.utils.buffered
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
        input.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries.first()
            assertTrue(entry is BooleanEntry)
            assertFalse(entry.value)
            assertEquals(expected = "boolean", actual = entry.name)
        }
    }

    @Test
    fun `parse string entry`() {
        input.buffered {
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
        input.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entryOne = entries[2]
            val entryTwo = entries[3]
            assertTrue(entryOne is IntEntry)
            assertTrue(entryTwo is IntEntry)
            assertEquals(expected = 0, actual = entryOne.value)
            assertEquals(expected = "another", actual = entryOne.name)
            assertEquals(expected = -1, actual = entryTwo.value)
            assertEquals(expected = "integer", actual = entryTwo.name)
        }
    }

    @Test
    fun `parse float entry`() {
        input.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[4]
            assertTrue(entry is FloatEntry)
            assertEquals(expected = 0.0f, actual = entry.value)
            assertEquals(expected = "float", actual = entry.name)
        }
    }

    @Test
    fun `parse long entry`() {
        input.buffered {
            val result = parser.parse(this)

            val entries = result.entries
            val entry = entries[6]
            assertTrue(entry is LongEntry)
            assertEquals(expected = 100L, actual = entry.value)
            assertEquals(expected = "long", actual = entry.name)
        }
    }

    @Test
    fun `parse set entry`() {
        input.buffered {
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

    private companion object {
        const val input =
            """<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
                <map>
                    <boolean name="boolean" value="false" />
                    <string name="string">string</string>
                    <int name="another" value="0" />
                    <int name="integer" value="-1" />
                    <float name="float" value="0.0" />
                    <set name="string-set">
                        <string>strings</string>
                        <string>one</string>
                        <string>two</string>
                        <string>three</string>
                    </set>
                    <long name="long" value="100" />
                </map>
            """
    }
}