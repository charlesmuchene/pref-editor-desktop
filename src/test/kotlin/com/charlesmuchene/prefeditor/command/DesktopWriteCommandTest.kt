package com.charlesmuchene.prefeditor.command

import com.charlesmuchene.prefeditor.data.Edit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.pathString
import kotlin.test.assertEquals

class DesktopWriteCommandTest {
    @TempDir
    private lateinit var path: Path

    private lateinit var command: DesktopWriteCommand

    @BeforeEach
    fun setup() {
        command = DesktopWriteCommand(path.pathString)
    }

    @Test
    fun `delete command ordering is correct`() {
        val edit = Edit.Delete(matcher = "matcher")

        val list =
            buildList {
                with(command) { delete(edit) }
            }

        assertEquals(listOf("sh", "desktop.sh", "delete", "matcher", path.pathString), list)
    }

    @Test
    fun `change command ordering is correct`() {
        val edit = Edit.Change(matcher = "matcher", content = "content")

        val list =
            buildList {
                with(command) { change(edit) }
            }

        assertEquals(listOf("sh", "desktop.sh", "change", "matcher", "content", path.pathString), list)
    }

    @Test
    fun `add command ordering is correct`() {
        val edit = Edit.Add(content = "content")

        val list =
            buildList {
                with(command) { add(edit = edit) }
            }

        assertEquals(listOf("sh", "desktop.sh", "add", "<\\/map>", "content", path.pathString), list)
    }
}
