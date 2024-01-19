package com.charlesmuchene.prefedit.parser

import okio.BufferedSource
import okio.buffer
import okio.source
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class DeviceListParserTest {

    private lateinit var parser: DeviceListParser

    @BeforeEach
    fun setup() {
        parser = DeviceListParser()
    }

    @Test
    fun `parses no device stream`() {
        NO_DEVICE.buffered {
            val devices = parser.parse(this)
            assertTrue(devices.isNotEmpty())
        }
    }

    @Test
    fun runner() {
        val tokens = "1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4".split(" ")
        println(tokens[0])
        println(tokens[1])
    }

    private fun String.buffered(block: BufferedSource.() -> Unit): Unit = byteInputStream().source().buffer().use(block)

    private companion object {
        val NO_DEVICE = """
            List of devices attached
        """.trimIndent()

        val ONE_DEVICE = """
            List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4
        """.trimIndent()

        val MULTIPLE_DEVICES = """
            List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:6
            emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
        """.trimIndent()

        val UNAUTHORIZED_DEVICE = """
            List of devices attached
            1B241CAA5079LR         unauthorized usb:1O845693Y transport_id:2
        """.trimIndent()

    }
}