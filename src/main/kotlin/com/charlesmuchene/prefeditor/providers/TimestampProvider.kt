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

package com.charlesmuchene.prefeditor.providers

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun interface TimestampProvider {
    operator fun invoke(): String
}

class TimeStampProviderImpl : TimestampProvider {

    override fun invoke(): String {
        val now = LocalDateTime.now()
        return now.format(formatter)
    }

    private companion object {
        const val PATTERN = "yyyy-MM-dd-HH:mm:ss"
        val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN)
    }
}