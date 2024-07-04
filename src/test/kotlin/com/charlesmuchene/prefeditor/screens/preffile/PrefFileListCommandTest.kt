package com.charlesmuchene.prefeditor.screens.preffile

import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import com.charlesmuchene.prefeditor.TestFixtures.app
import com.charlesmuchene.prefeditor.TestFixtures.device
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class PrefFileListCommandTest {
    @Test
    fun `pref file list command for key-value preferences is valid`() {
        val expected = "$EXECUTABLE -s ${device.serial} shell run-as ${app.packageName} ls shared_prefs"
        val command = KeyValuePrefFileListCommand(app = app, device = device, executable = EXECUTABLE)

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }

    @Test
    fun `pref file list command for datastore preferences is valid`() {
        val expected = "$EXECUTABLE -s ${device.serial} shell run-as ${app.packageName} ls files/datastore"
        val command = DatastorePrefFileListCommand(app = app, device = device, executable = EXECUTABLE)

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }
}
