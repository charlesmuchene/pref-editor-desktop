package com.charlesmuchene.prefeditor.providers

import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class TimeStampProviderImplTest {
    @Test
    fun `timestamp is formatted correctly`() {
        val timestamp = TimeStampProviderImpl()

        val output = timestamp()

        assertTrue(output.matches("\\d{4}-\\d{2}-\\d{2}-\\d{2}:\\d{2}:\\d{2}".toRegex()))
    }
}
