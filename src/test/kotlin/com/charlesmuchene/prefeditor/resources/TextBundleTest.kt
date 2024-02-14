package com.charlesmuchene.prefeditor.resources

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class TextBundleTest {

    private lateinit var bundle: TextBundle

    @BeforeEach
    fun setup() {
        bundle = TextBundle()
    }

    @Test
    fun `bundle reads properties from resources`() {
        val name = bundle[TestKeys()]

        assertEquals(expected = "muchene", actual = name)
    }

    @Test
    fun `bundle returns default string for missing key`() {
        val defaultText = bundle[TestKeys(key = "missing-key")]

        assertEquals(expected = "--", actual = defaultText)
    }

    private class TestKeys(override val key: String = "charles") : TextKey
}