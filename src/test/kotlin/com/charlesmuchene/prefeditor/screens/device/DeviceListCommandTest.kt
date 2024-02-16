package com.charlesmuchene.prefeditor.screens.device

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DeviceListCommandTest {
    @Test
    fun `device list command is valid`() {
        val expected = "adb devices -l"
        val command = DeviceListCommand()

        val output = command.command()

        assertEquals(expected = expected, actual = output.joinToString(" "))
    }
}
