package com.charlesmuchene.prefeditor.screens.preferences.device

import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import com.charlesmuchene.prefeditor.TestFixtures.app
import com.charlesmuchene.prefeditor.TestFixtures.device
import com.charlesmuchene.prefeditor.TestFixtures.prefFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PreferencesCommandTest {
    @Test
    fun `preferences command is valid`() {
        val expected =
            "$EXECUTABLE -s ${device.serial} exec-out run-as ${app.packageName} " +
                "cat /data/data/${app.packageName}/shared_prefs/${prefFile.name}"
        val command = PreferencesCommand(app = app, device = device, prefFile = prefFile, executable = EXECUTABLE)

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }
}
