package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.utils.buffered
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrefFilesParserTest {

    private lateinit var parser: PrefFilesParser

    @BeforeEach
    fun setup() {
        parser = PrefFilesParser()
    }

    @Test
    fun `empty preferences for missing preference directory`() {
        "ls: shared_prefs: No such file or directory".buffered {
            val result = parser.parse(this)
            assertTrue(result.isEmpty())
        }
    }

    @Test
    fun `empty preferences with no files in the directory`() {
        "".buffered {
            val result = parser.parse(this)
            assertTrue(result.isEmpty())
        }
    }
}