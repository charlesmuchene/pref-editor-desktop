package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.Devices
import okio.BufferedSource
import java.io.BufferedReader

class DeviceListParser : Parser<Devices> {

    override fun parse(reader: BufferedSource): Devices {
        return emptyList()
    }

}

/**
 * // TODO Handle unauthorized device
 * List of devices attached
 * 0A281FDD4001LR         unauthorized usb:17825792X transport_id:2
 * emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
 *
 * List of devices attached
 * 0A281FDD4001LR         device usb:17825792X product:redfin model:Pixel_5 device:redfin transport_id:2
 * emulator-5554          device product:sdk_gphone64_arm64 model:sdk_gphone64_arm64 device:emu64a transport_id:1
 */