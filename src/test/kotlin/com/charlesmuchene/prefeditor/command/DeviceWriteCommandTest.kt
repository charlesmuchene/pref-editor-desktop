package com.charlesmuchene.prefeditor.command

import androidx.compose.runtime.mutableStateOf
import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.data.Edit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeviceWriteCommandTest {
    private lateinit var command: DeviceWriteCommand
    private val backup = mutableStateOf(false)

    @BeforeEach
    fun setup() {
        command =
            DeviceWriteCommand(
                app = TestFixtures.app,
                device = TestFixtures.device,
                file = TestFixtures.prefFile,
                backup = backup,
                timestamp = { "now" },
            )
    }

    @Test
    fun `delete command ordering is correct`() {
        val edit = Edit.Delete(matcher = "matcher")

        val list =
            buildList {
                with(command) {
                    delete(edit)
                }
            }

        assertEquals(
            listOf(
                "sh",
                "device.sh",
                "delete",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "i",
                "matcher",
                "preferences.xml",
            ),
            list,
        )
    }

    @Test
    fun `change command ordering is correct`() {
        val edit = Edit.Change(matcher = "matcher", content = "content")

        val list =
            buildList {
                with(command) {
                    change(edit)
                }
            }

        assertEquals(
            listOf(
                "sh",
                "device.sh",
                "change",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "i",
                "matcher",
                "content",
                "preferences.xml",
            ),
            list,
        )
    }

    @Test
    fun `add command ordering is correct`() {
        val edit = Edit.Add(content = "content")

        val list =
            buildList {
                with(command) {
                    add(edit = edit)
                }
            }

        assertEquals(
            listOf(
                "sh",
                "device.sh",
                "add",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "i",
                "<\\/map>",
                "content",
                "preferences.xml",
            ),
            list,
        )
    }

    @Test
    fun `command backs up file before editing`() {
        backup.value = true
        val edit = Edit.Add(content = "content")

        val list =
            buildList {
                with(command) {
                    add(edit = edit)
                }
            }

        assertEquals(
            listOf(
                "sh",
                "device.sh",
                "add",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "i.backup-now",
                "<\\/map>",
                "content",
                "preferences.xml",
            ),
            list,
        )
    }
}
