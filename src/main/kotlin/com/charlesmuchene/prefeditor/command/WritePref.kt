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

package com.charlesmuchene.prefeditor.command

import com.charlesmuchene.prefeditor.data.*
import com.charlesmuchene.prefeditor.data.Tags.BOOLEAN
import com.charlesmuchene.prefeditor.data.Tags.FLOAT
import com.charlesmuchene.prefeditor.data.Tags.INT
import com.charlesmuchene.prefeditor.data.Tags.LONG
import com.charlesmuchene.prefeditor.data.Tags.SET
import com.charlesmuchene.prefeditor.data.Tags.STRING
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder
import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.providers.TimeStampProviderImpl
import com.charlesmuchene.prefeditor.providers.TimestampProvider
import okio.BufferedSource
import org.xmlpull.v1.XmlSerializer

data class WritePref(
    private val app: App,
    private val device: Device,
    private val prefFile: PrefFile,
    private val enableBackup: Boolean,
    private val preferences: Preferences,
    private val writer: PreferenceEncoder = PreferencesCodec(),
    private val timestampProvider: TimestampProvider = TimeStampProviderImpl(),
) : WriteCommand<Boolean> {

    private val backupInput = if (enableBackup) "i.backup_${timestampProvider()}" else "i"

    override val command: String by lazy {
        "sh push.sh ${device.serial} ${app.packageName} $backupInput ${prefFile.name}"
    }

    override val content: String by lazy {
        writer.encodeDocument {
            preferences.preferences.forEach { preference ->
                when (preference) {
                    is BooleanPreference -> tag(BOOLEAN) { attribute(name = preference.name, value = preference.value) }
                    is FloatPreference -> tag(FLOAT) { attribute(name = preference.name, value = preference.value) }
                    is LongPreference -> tag(LONG) { attribute(name = preference.name, value = preference.value) }
                    is IntPreference -> tag(INT) { attribute(name = preference.name, value = preference.value) }

                    is StringPreference -> tag(STRING) {
                        attribute(null, NAME, preference.name)
                        text(preference.value)
                    }

                    is SetPreference -> tag(SET) {
                        attribute(null, NAME, preference.name)
                        preference.entries.forEach { string ->
                            tag(STRING) { text(string) }
                        }
                    }
                }
            }
        }
    }

    override suspend fun execute(source: BufferedSource): Boolean = source.readUtf8().isBlank()

    private fun XmlSerializer.attribute(name: String, value: String) {
        attribute(null, NAME, name)
        attribute(null, VALUE, value)
    }

    companion object {
        const val VALUE = "value"
        const val NAME = "name"
    }
}
