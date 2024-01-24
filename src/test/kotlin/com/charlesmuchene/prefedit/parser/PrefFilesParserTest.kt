package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.PrefFile
import com.charlesmuchene.prefedit.utils.buffered
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrefFilesParserTest {

    private lateinit var parser: PrefFilesParser

    @BeforeEach
    fun setup() {
        parser = PrefFilesParser()
    }

    @Test
    fun `empty preferences when we are missing preference directory`() {
        "ls: shared_prefs: No such file or directory".buffered {
            val result = parser.parse(this)
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `empty preferences when there are no files in the directory`() {
        "".buffered {
            val result = parser.parse(this)
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `preference listing when preference directory is non empty`() {
        """com.charlesmuchene.player_preferences.xml
            named.xml
        """.buffered {
            val result = parser.parse(this)
            assertEquals(expected = 2, actual = result.size)
            assertEquals(expected = "com.charlesmuchene.player_preferences.xml", actual = result.first().name)
            assertEquals(expected = PrefFile.Type.KEY_VALUE, actual = result.first().type)
        }
    }

    @Test
    fun `preference listing when app is not debuggable`() {
        "run-as: package not debuggable:".buffered {
            val result = parser.parse(this)
            assertTrue(result.isEmpty())
        }
    }
}