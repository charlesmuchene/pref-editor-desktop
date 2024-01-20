package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Device.Type
import com.charlesmuchene.prefedit.data.Devices
import okio.BufferedSource

class DeviceListParser : Parser<Devices> {

    // TODO parse wifi-connected device
    override fun parse(source: BufferedSource): Devices = buildList {
        source.readUtf8Line() // discard header
        while (true) {
            val line = source.readUtf8Line() ?: break
            add(parseDevice(line = line))
        }
    }

    private fun parseDevice(line: String): Device {
        val tokens = line.split(" ").filterNot(String::isEmpty)
        val attributes = tokens.subList(fromIndex = 2, toIndex = tokens.size).map {
            val value = it.split(":")
            Device.Attribute(name = value[0], value = value[1])
        }
        val type = when (tokens[1]) {
            DEVICE -> Type.Device
            UNAUTHORIZED -> Type.Unauthorized
            else -> Type.Unknown
        }
        return Device(serial = tokens[0], type = type, attributes = attributes)
    }

    private companion object {
        const val DEVICE = "device"
        const val UNAUTHORIZED = "unauthorized"
    }
}