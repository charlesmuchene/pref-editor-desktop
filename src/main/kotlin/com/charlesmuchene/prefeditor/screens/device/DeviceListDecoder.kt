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

package com.charlesmuchene.prefeditor.screens.device

import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.Devices
import com.charlesmuchene.prefeditor.exceptions.DecodeException
import kotlinx.coroutines.yield

class DeviceListDecoder {
    suspend fun decode(content: String): Result<Devices> =
        if (content.contains(DEVICE_RESULT_HEADER)) {
            Result.success(buildList { parseDevices(content) })
        } else {
            Result.failure(DecodeException(content))
        }

    private suspend fun MutableList<Device>.parseDevices(content: String) {
        content.lineSequence()
            .drop(1) // drop header
            .filter(String::isNotBlank)
            .forEach { line ->
                yield()
                add(parseDevice(line = line))
            }
    }

    private fun parseDevice(line: String): Device {
        require(line.isNotBlank())
        val tokens = line.split(DELIMITER).filterNot(String::isEmpty)
        val attributesIndex = tokens.indexOfFirst { token -> token.contains(ATTRIBUTE_DELIMITER) }
        val attributes =
            tokens.subList(fromIndex = attributesIndex, toIndex = tokens.size).map {
                val value = it.split(ATTRIBUTE_DELIMITER)
                Device.Attribute(name = value[0], value = value[1])
            }
        val type =
            when (tokens[1]) {
                DEVICE -> Device.Type.Device
                UNAUTHORIZED -> Device.Type.Unauthorized
                else -> Device.Type.Unknown
            }
        return Device(serial = tokens[0], type = type, attributes = attributes)
    }

    private companion object {
        const val DEVICE = "device"
        const val DELIMITER = " "
        const val ATTRIBUTE_DELIMITER = ":"
        const val UNAUTHORIZED = "unauthorized"
        const val DEVICE_RESULT_HEADER = "List of devices attached"
    }
}
