package com.charlesmuchene.prefeditor.preferences

import com.charlesmuchene.prefeditor.preferences.PreferenceWriter.Writer.attrib
import com.charlesmuchene.prefeditor.preferences.PreferenceWriter.Writer.tag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PreferenceManagerTest {

    private lateinit var manager: PreferenceManager

    @BeforeEach
    fun setup() {
        manager = PreferenceManager()
    }

    @Test
    fun `write document creates an empty document`() {
        val output = manager.writeDocument()
        assertEquals(expected = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\r\n<map />", actual = output)
    }

    @Test
    fun `write single tag`() {
        val output = manager.writeContent { tag("tag") { attrib("name", "value") } }
        assertEquals(expected = "<tag name=\"value\" />", actual = output)
    }

    @Test
    fun `write xml content`() {
        val output = manager.writeContent {
            tag("tag") { attrib("name", "value") }
            tag("stuff") { text("content") }
        }
        assertEquals(expected = "<tag name=\"value\" />\r\n<stuff>content</stuff>", actual = output.trim())
    }
}