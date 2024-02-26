package com.charlesmuchene.prefeditor.screens.device

import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeviceListCommandTest {
    @Test
    fun `device list command is valid`() {
        val expected = "$EXECUTABLE devices -l"
        val command = DeviceListCommand(executable = EXECUTABLE)

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }
}
