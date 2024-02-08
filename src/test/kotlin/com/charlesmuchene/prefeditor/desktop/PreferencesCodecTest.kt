package com.charlesmuchene.prefeditor.desktop

import com.charlesmuchene.prefeditor.screens.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PreferencesCodecTest {

    private lateinit var manager: PreferencesCodec

    @BeforeEach
    fun setup() {
        manager = PreferencesCodec()
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