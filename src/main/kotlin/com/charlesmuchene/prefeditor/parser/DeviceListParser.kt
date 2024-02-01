/*
 * Copyright (c) 2024 Charles Muchene
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.charlesmuchene.prefeditor.parser

import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Device.Type
import com.charlesmuchene.prefeditor.data.Devices
import kotlinx.coroutines.yield
import okio.BufferedSource

class DeviceListParser : Parser<Devices> {

    // TODO parse wifi-connected device
    override suspend fun parse(source: BufferedSource): Devices = buildList {
        source.readUtf8Line() // discard header
        while (true) {
            yield()
            val line = source.readUtf8Line() ?: break
            if (line.isNotBlank()) add(parseDevice(line = line))
        }
    }

    private fun parseDevice(line: String): Device {
        require(line.isNotBlank())
        val tokens = line.split(" ").filterNot(String::isEmpty)
        val attributesIndex = tokens.indexOfFirst { token -> token.contains(ATTRIBUTE_DELIMITER) }
        val attributes = tokens.subList(fromIndex = attributesIndex, toIndex = tokens.size).map {
            val value = it.split(ATTRIBUTE_DELIMITER)
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
        const val ATTRIBUTE_DELIMITER = ":"
        const val UNAUTHORIZED = "unauthorized"
    }
}