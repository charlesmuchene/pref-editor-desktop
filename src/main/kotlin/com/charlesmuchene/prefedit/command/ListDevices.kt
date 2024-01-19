package com.charlesmuchene.prefedit.command

import com.charlesmuchene.prefedit.data.Devices
import com.charlesmuchene.prefedit.parser.DeviceListParser
import com.charlesmuchene.prefedit.parser.Parser

data class ListDevices(override val parser: Parser<Devices> = DeviceListParser()) : Command<Devices> {
    override val command: String = "devices -l"
}