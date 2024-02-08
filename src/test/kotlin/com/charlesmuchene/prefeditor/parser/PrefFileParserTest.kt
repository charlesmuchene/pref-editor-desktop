package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.utils.buffered
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrefFileParserTest {

    private lateinit var parser: PrefFilesParser

    @BeforeEach
    fun setup() {
        parser = PrefFilesParser()
    }

    @Test
    fun `empty preferences when we are missing preference directory`() {
        "ls: shared_prefs: No such file or directory".buffered {
            val result = parser.parse(this)
            assertTrue(result is EmptyPrefs)
        }
    }

    @Test
    fun `empty preferences when there are no files in the directory`() {
        "".buffered {
            val result = parser.parse(this)
            assertTrue(result is EmptyPrefs)
        }
    }

    @Test
    fun `preference listing when preference directory is non empty`() {
        """com.charlesmuchene.player_preferences.xml
            named.xml
        """.trimIndent().buffered {
            val result = parser.parse(this)
            assertTrue(result is Files)
            val files = (result as Files).files
            assertEquals(expected = 2, actual = files.size)
            assertEquals(expected = "com.charlesmuchene.player_preferences.xml", actual = files.first().name)
            assertEquals(expected = PrefFile.Type.KEY_VALUE, actual = files.first().type)
        }
    }

    @Test
    fun `preference listing when app is not debuggable`() {
        "run-as: package not debuggable:".buffered {
            val result = parser.parse(this)
            assertTrue(result is NonDebuggable)
        }
    }
}