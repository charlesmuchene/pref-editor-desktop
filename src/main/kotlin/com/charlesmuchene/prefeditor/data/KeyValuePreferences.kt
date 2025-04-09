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

package com.charlesmuchene.prefeditor.data

import com.charlesmuchene.datastore.preferences.Preference
import com.charlesmuchene.datastore.preferences.exceptions.MalformedContentException
import com.charlesmuchene.datastore.preferences.parsePreferences
import io.github.oshai.kotlinlogging.KotlinLogging

sealed interface Preferences

data class KeyValuePreferences(val preferences: List<Preference>) : Preferences

class DatastorePreferences(val content: ByteArray) : Preferences {

    private val logger = KotlinLogging.logger { }

    fun parse(): List<Preference> = try {
        parsePreferences(content)
    } catch (e: MalformedContentException) {
        logger.error(e) { e.message }
        emptyList()
    }
}
