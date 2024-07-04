package com.charlesmuchene.prefeditor.screens.preffile

import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.Files
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.NoFiles
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.NonDebuggable
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class PrefFileListDecoderTest {
    private lateinit var decoder: PrefFileListDecoder

    @BeforeEach
    fun setup() {
        decoder = PrefFileListDecoder()
    }

    @Test
    fun `empty preferences when we are missing preference directory`() =
        runTest {
            val result = decoder.decode(listOf("ls: shared_prefs: No such file or directory"))
            assertTrue(result is NoFiles)
        }

    @Test
    fun `empty preferences when there are no files in the directory`() =
        runTest {
            val result = decoder.decode(listOf(""))
            assertTrue(result is NoFiles)
        }

    @Test
    fun `preference listing when preference directory is non empty`() =
        runTest {
            val content =
                """
                com.charlesmuchene.player_preferences.xml
                named.xml
                """.trimIndent()
            val result = decoder.decode(listOf(content))
            assertTrue(result is Files)
            val files = (result as Files).files
            kotlin.test.assertEquals(expected = 2, actual = files.size)
            kotlin.test.assertEquals(
                expected = "com.charlesmuchene.player_preferences.xml",
                actual = files.first().name,
            )
            kotlin.test.assertEquals(expected = PrefFile.Type.KEY_VALUE, actual = files.first().type)
        }

    @Test
    fun `preference listing when app is not debuggable`() =
        runTest {
            val result = decoder.decode(listOf("run-as: package not debuggable:"))
            assertTrue(result is NonDebuggable)
        }
}
