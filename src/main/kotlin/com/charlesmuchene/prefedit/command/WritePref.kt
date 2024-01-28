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

package com.charlesmuchene.prefedit.command

import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.data.Tags.BOOLEAN
import com.charlesmuchene.prefedit.data.Tags.FLOAT
import com.charlesmuchene.prefedit.data.Tags.INT
import com.charlesmuchene.prefedit.data.Tags.LONG
import com.charlesmuchene.prefedit.data.Tags.ROOT
import com.charlesmuchene.prefedit.data.Tags.SET
import com.charlesmuchene.prefedit.data.Tags.STRING
import com.charlesmuchene.prefedit.files.PrefEditFiles
import com.charlesmuchene.prefedit.providers.TimeStampProviderImpl
import com.charlesmuchene.prefedit.providers.TimestampProvider
import okio.Buffer
import okio.BufferedSource
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer

data class WritePref(
    private val app: App,
    private val device: Device,
    private val prefFile: PrefFile,
    private val enableBackup: Boolean,
    private val preferences: Preferences,
    private val timestampProvider: TimestampProvider = TimeStampProviderImpl(),
) : WriteCommand<Boolean> {

    init {
        PrefEditFiles.copyPushScript()
    }

    private val backupInput = if (enableBackup) "i.backup_${timestampProvider()}" else "i"

    override val command: String by lazy {
        "sh push.sh ${device.serial} ${app.packageName} $backupInput ${prefFile.name}"
    }

    override val content: String by lazy {
        buildContent {
            preferences.entries.forEach { entry ->
                when (entry) {
                    is BooleanEntry -> tag(BOOLEAN) { attribute(name = entry.name, value = entry.value) }
                    is FloatEntry -> tag(FLOAT) { attribute(name = entry.name, value = entry.value) }
                    is LongEntry -> tag(LONG) { attribute(name = entry.name, value = entry.value) }
                    is IntEntry -> tag(INT) { attribute(name = entry.name, value = entry.value) }

                    is StringEntry -> tag(STRING) {
                        attribute(null, NAME, entry.name)
                        text(entry.value)
                    }

                    is SetEntry -> tag(SET) {
                        attribute(null, NAME, entry.name)
                        entry.entries.forEach { string ->
                            tag(STRING) { text(string) }
                        }
                    }
                }
            }
        }
    }

    override fun execute(source: BufferedSource): Boolean = source.readUtf8().isBlank()

    private fun XmlSerializer.attribute(name: String, value: String) {
        attribute(null, NAME, name)
        attribute(null, VALUE, value)
    }

    private fun XmlSerializer.tag(tag: String, block: XmlSerializer.() -> Unit) {
        startTag(null, tag)
        block()
        endTag(null, tag)
    }

    private fun buildContent(block: XmlSerializer.() -> Unit): String {
        val buffer = Buffer()
        with(XmlPullParserFactory.newInstance().newSerializer()) {
            setOutput(buffer.outputStream(), ENCODING)
            setFeature(INDENTATION_FEATURE, true)
            startDocument(ENCODING, true)
            startTag(null, ROOT)
            block()
            endDocument()
        }
        return buffer.readUtf8()
    }

    companion object {
        const val INDENTATION_FEATURE = "http://xmlpull.org/v1/doc/features.html#indent-output"
        const val ENCODING = "utf-8"
        const val VALUE = "value"
        const val NAME = "name"
    }
}
