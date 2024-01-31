package com.charlesmuchene.prefeditor.preferences

import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PreferenceCodecTest {

    private lateinit var manager: PreferenceCodec

    @BeforeEach
    fun setup() {
        manager = PreferenceCodec()
    }

    @Test
    fun `write document creates an empty document`() {
        val output = manager.encodeDocument()
        assertEquals(expected = "<?xml version='1.0' encoding='utf-8' standalone='yes' ?>\r\n<map />", actual = output)
    }

    @Test
    fun `write single tag`() {
        val output = manager.encode { tag("tag") { attrib("name", "value") } }
        assertEquals(expected = "<tag name=\"value\" />", actual = output)
    }

    @Test
    fun `write xml content`() {
        val output = manager.encode {
            tag("tag") { attrib("name", "value") }
            tag("stuff") { text("content") }
        }
        assertEquals(expected = "<tag name=\"value\" />\r\n<stuff>content</stuff>", actual = output.trim())
    }
}