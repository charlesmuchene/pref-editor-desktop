package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.utils.buffered
import okio.BufferedSource
import okio.buffer
import okio.source
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
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
            assertTrue(devices.isEmpty())
        }
    }

    @Test
    fun `parsing one device`() {
        ONE_DEVICE.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(element = deviceOne), actual = devices)
        }
    }

    @Test
    fun `parsing multiple devices`() {
        MULTIPLE_DEVICES.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(deviceOne, deviceTwo), actual = devices)
        }
    }

    @Test
    fun `parsing multiple devices with an unauthorized entry`() {
        UNAUTHORIZED_DEVICE.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(unauthorized, deviceTwo), actual = devices)
        }
    }

    private companion object {
        private val NO_DEVICE = """List of devices attached
            
        """

        private val ONE_DEVICE = """List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4
            
        """

        private val MULTIPLE_DEVICES = """List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4
            emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
            
        """

        private val UNAUTHORIZED_DEVICE = """List of devices attached
            1B241CAA5079LR         unauthorized usb:1O845693Y transport_id:4
            emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
            
        """

        val deviceOne = Device(serial = "1B241CAA5079LR", type = Device.Type.Device, attributes = listOf(
            Device.Attribute(name = "usb", value = "1O845693Y"),
            Device.Attribute(name = "product", value = "redfin"),
            Device.Attribute(name = "model", value = "Pixel_5"),
            Device.Attribute(name = "device", value = "redfin"),
            Device.Attribute(name = "transport_id", value = "4"),
        ))

        val deviceTwo = Device(serial = "emulator-5554", type = Device.Type.Device, attributes = listOf(
            Device.Attribute(name = "product", value = "sdk_gphone64_arm64"),
            Device.Attribute(name = "model", value = "sdk_gphone64_arm64"),
            Device.Attribute(name = "device", value = "emu64a"),
            Device.Attribute(name = "transport_id", value = "1"),
        ))

        val unauthorized = Device(serial = "1B241CAA5079LR", type = Device.Type.Unauthorized, attributes = listOf(
            Device.Attribute(name = "usb", value = "1O845693Y"),
            Device.Attribute(name = "transport_id", value = "4"),
        ))

    }
}