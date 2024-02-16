package com.charlesmuchene.prefeditor.screens.apps

import com.charlesmuchene.prefeditor.TestFixtures.APP_LIST_OUTPUT
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppListDecoderTest {
    @Test
    fun `decoder returns a sorted list of apps`() =
        runTest {
            val decoder = AppListDecoder()

            val apps = decoder.decode(APP_LIST_OUTPUT)

            assertEquals(expected = 5, actual = apps.size)
            assertTrue(apps.last().packageName.contains(other = "works"))
            assertTrue(apps.first().packageName.contains(other = "compose"))
        }

    @Test
    fun `decoder returns apps list in declared order`() =
        runTest {
            val decoder = AppListDecoder(sorted = false)

            val apps = decoder.decode(APP_LIST_OUTPUT)

            assertEquals(expected = 5, actual = apps.size)
            assertTrue(apps.first().packageName.contains(other = "player"))
            assertTrue(apps.last().packageName.contains(other = "compose"))
        }
}
