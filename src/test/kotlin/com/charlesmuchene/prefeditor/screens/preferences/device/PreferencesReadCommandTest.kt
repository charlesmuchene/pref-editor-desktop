package com.charlesmuchene.prefeditor.screens.preferences.device

import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import com.charlesmuchene.prefeditor.TestFixtures.app
import com.charlesmuchene.prefeditor.TestFixtures.datastorePrefFile
import com.charlesmuchene.prefeditor.TestFixtures.device
import com.charlesmuchene.prefeditor.TestFixtures.keyValuePrefFile
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PreferencesReadCommandTest {
    @Test
    fun `preferences command is valid`() {
        assertTrue { keyValuePrefFile.name.endsWith(suffix = ".xml") }
        val expected =
            "$EXECUTABLE -s ${device.serial} exec-out run-as ${app.packageName} " +
                "cat /data/data/${app.packageName}/shared_prefs/${keyValuePrefFile.name}"
        val command = PreferencesReadCommand(
            app = app,
            device = device,
            executable = EXECUTABLE,
            prefFile = keyValuePrefFile,
        )

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }

    @Test
    fun `preferences command for datastore pref file has a valid datastore command`() {
        assertTrue { datastorePrefFile.name.endsWith(suffix = ".preferences_pb") }
        val expected =
            "$EXECUTABLE -s ${device.serial} exec-out run-as ${app.packageName} " +
                "cat /data/data/${app.packageName}/files/datastore/${datastorePrefFile.name}"
        val command = PreferencesReadCommand(
            app = app,
            device = device,
            prefFile = datastorePrefFile,
            executable = EXECUTABLE
        )

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }
}
