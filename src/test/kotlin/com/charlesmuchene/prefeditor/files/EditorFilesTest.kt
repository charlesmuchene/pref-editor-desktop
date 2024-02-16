package com.charlesmuchene.prefeditor.files

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.files.EditorFiles.DESKTOP_FILE
import com.charlesmuchene.prefeditor.files.EditorFiles.DEVICE_FILE
import com.charlesmuchene.prefeditor.files.EditorFiles.PREFERENCES
import com.charlesmuchene.prefeditor.files.EditorFiles.ROOT_DIR
import com.charlesmuchene.prefeditor.files.EditorFiles.SCRIPTS_DIR
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EditorFilesTest {
    @TempDir
    private lateinit var appPath: Path

    private val codec = PreferencesCodec()

    @Test
    fun `initialize creates app prefs and copies scripts`() =
        runTest {
            EditorFiles.initialize(codec = codec, appPathOverride = appPath)

            val root = appPath.resolve(ROOT_DIR)
            assertTrue(root.exists())
            assertTrue(root.resolve(PREFERENCES).exists())

            val scripts = root.resolve(SCRIPTS_DIR)
            assertTrue(scripts.exists())
            assertTrue(scripts.resolve(DEVICE_FILE).exists())
            assertTrue(scripts.resolve(DESKTOP_FILE).exists())

            val emptyAppPreferences = Files.readString(root.resolve(PREFERENCES))
            assertEquals(expected = TestFixtures.emptyPreferences(), actual = emptyAppPreferences)
        }
}
