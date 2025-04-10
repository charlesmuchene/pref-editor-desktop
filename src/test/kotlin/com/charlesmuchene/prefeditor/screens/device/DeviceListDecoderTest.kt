package com.charlesmuchene.prefeditor.screens.device

import com.charlesmuchene.prefeditor.TestFixtures.deviceOne
import com.charlesmuchene.prefeditor.TestFixtures.deviceTwo
import com.charlesmuchene.prefeditor.TestFixtures.unauthorized
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class DeviceListDecoderTest {
    private lateinit var decoder: DeviceListDecoder

    @BeforeEach
    fun setup() {
        decoder = DeviceListDecoder()
    }

    @Test
    fun `parses empty devices stream`() =
        runTest {
            val result = decoder.decode(EMPTY_DEVICES)
            assertTrue(result.isSuccess)
            assertTrue(result.getOrThrow().isEmpty())
        }

    @Test
    fun `parsing one device`() =
        runTest {
            val result = decoder.decode(ONE_DEVICE)
            assertEquals(expected = listOf(element = deviceOne), actual = result.getOrThrow())
        }

    @Test
    fun `parsing multiple devices`() =
        runTest {
            val result = decoder.decode(MULTIPLE_DEVICES)
            assertEquals(expected = listOf(deviceOne, deviceTwo), actual = result.getOrThrow())
        }

    @Test
    fun `parsing multiple devices with an unauthorized preference`() =
        runTest {
            val result = decoder.decode(UNAUTHORIZED_DEVICE)
            assertEquals(expected = listOf(unauthorized, deviceTwo), actual = result.getOrThrow())
        }

    @Test
    fun `parsing rare device connection`() =
        runTest {
            val result = decoder.decode(NEVER_SEEN_BEFORE_DEVICE_CONNECTION)
            assertEquals(expected = deviceOne.serial, actual = result.getOrThrow().first().serial)
        }

    private companion object {
        private const val EMPTY_DEVICES = """List of devices attached
            
        """

        private const val ONE_DEVICE = """List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4
            
        """

        private const val MULTIPLE_DEVICES = """List of devices attached
            1B241CAA5079LR         device usb:1O845693Y product:redfin model:Pixel_5 device:redfin transport_id:4
            emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
            
        """

        private const val UNAUTHORIZED_DEVICE = """List of devices attached
            1B241CAA5079LR         unauthorized usb:1O845693Y transport_id:4
            emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
            
        """

        private val NEVER_SEEN_BEFORE_DEVICE_CONNECTION =
            """
            List of devices attached
            1B241CAA5079LR         device 1-1 product:redfin model:Pixel_5 device:redfin transport_id:1
            """.trimIndent()
    }
}
