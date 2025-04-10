package com.charlesmuchene.prefeditor.command

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import com.charlesmuchene.prefeditor.data.Edit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeviceWriteCommandTest {
    private lateinit var command: DeviceWriteCommand

    @BeforeEach
    fun setup() {
        command =
            DeviceWriteCommand(
                app = TestFixtures.app,
                device = TestFixtures.device,
                file = TestFixtures.keyValuePrefFile,
                timestamp = { "now" },
                executable = EXECUTABLE,
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
                "exec",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "shared_prefs/preferences.xml",
                "delete",
                "i",
                "matcher",
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
                "exec",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "shared_prefs/preferences.xml",
                "change",
                "i",
                "matcher",
                "content",
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
                "exec",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "shared_prefs/preferences.xml",
                "add",
                "i",
                "<\\/map>",
                "content",
            ),
            list,
        )
    }

    @Test
    fun `backup command is correctly formed`() {
        val commandList = command.command(Edit.Backup)

        assertEquals(
            listOf(
                "sh",
                "device.sh",
                "exec",
                "1B241CAA5079LR",
                "com.charlesmuchene.pref-editor",
                "shared_prefs/preferences.xml",
                "backup",
                "shared_prefs/preferences.xml.backup-now.xml",
            ),
            commandList,
        )
    }
}
