package com.charlesmuchene.prefeditor.screens.apps

import com.charlesmuchene.prefeditor.TestFixtures
import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AppListCommandTest {
    @Test
    fun `app listing command is valid`() {
        val device = TestFixtures.device
        val expected = "$EXECUTABLE -s ${device.serial} shell cmd package list packages -3 --user 0"
        val command = AppListCommand(device = device, executable = EXECUTABLE)

        val list = command.command()

        assertEquals(expected = expected, actual = list.joinToString(" "))
    }
}
