package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.utils.buffered
import kotlinx.coroutines.test.runTest
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
    fun `parses no device stream`() = runTest {
        NO_DEVICE.buffered {
            val devices = parser.parse(this)
            assertTrue(devices.isEmpty())
        }
    }

    @Test
    fun `parsing one device`() = runTest {
        ONE_DEVICE.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(element = deviceOne), actual = devices)
        }
    }

    @Test
    fun `parsing multiple devices`() = runTest {
        MULTIPLE_DEVICES.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(deviceOne, deviceTwo), actual = devices)
        }
    }

    @Test
    fun `parsing multiple devices with an unauthorized preference`() = runTest {
        UNAUTHORIZED_DEVICE.buffered {
            val devices = parser.parse(this)
            assertEquals(expected = listOf(unauthorized, deviceTwo), actual = devices)
        }
    }

    @Test
    fun `parsing rare device connection`() = runTest {
        NEVER_SEEN_BEFORE_DEVICE_CONNECTION.buffered {
            val devices = parser.parse(this).first()
            assertEquals(expected = deviceOne.serial, actual = devices.serial)
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

        private val NEVER_SEEN_BEFORE_DEVICE_CONNECTION = """List of devices attached
            1B241CAA5079LR         device 1-1 product:redfin model:Pixel_5 device:redfin transport_id:1
        """.trimIndent()

        val deviceOne = Device(
            serial = "1B241CAA5079LR", type = Device.Type.Device, attributes = listOf(
                Device.Attribute(name = "usb", value = "1O845693Y"),
                Device.Attribute(name = "product", value = "redfin"),
                Device.Attribute(name = "model", value = "Pixel_5"),
                Device.Attribute(name = "device", value = "redfin"),
                Device.Attribute(name = "transport_id", value = "4"),
            )
        )

        val deviceTwo = Device(
            serial = "emulator-5554", type = Device.Type.Device, attributes = listOf(
                Device.Attribute(name = "product", value = "sdk_gphone64_arm64"),
                Device.Attribute(name = "model", value = "sdk_gphone64_arm64"),
                Device.Attribute(name = "device", value = "emu64a"),
                Device.Attribute(name = "transport_id", value = "1"),
            )
        )

        val unauthorized = Device(
            serial = "1B241CAA5079LR", type = Device.Type.Unauthorized, attributes = listOf(
                Device.Attribute(name = "usb", value = "1O845693Y"),
                Device.Attribute(name = "transport_id", value = "4"),
            )
        )

    }
}