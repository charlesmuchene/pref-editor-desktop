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
import com.charlesmuchene.prefedit.parser.NoOpParser
import com.charlesmuchene.prefedit.parser.Parser
import org.xmlpull.v1.XmlPullParserFactory
import org.xmlpull.v1.XmlSerializer
import java.io.ByteArrayOutputStream
import java.io.OutputStream

data class WritePref(
    private val app: App,
    private val device: Device,
    private val prefFile: PrefFile,
    private val preferences: Preferences,
) : Command<Unit> {
    override val parser: Parser<Unit> = NoOpParser
    override val command: String by lazy {
        buildString {
            append("-s ", device.serial, " exec-out run-as ", app.packageName, " sed -Ei ")
            append("-e '1s", SEP, ".*", SEP, PATTERN, SEP, "g'")
            append("-e '", SEP, "^", PATTERN, SEP, "!d'")
            append("-e '", SEP, PATTERN, SEP, content(), SEP, "g")
            append(" '/data/data/${app.packageName}/shared_prefs/", prefFile.name, "'")
        }
    }

    private fun content(): String = buildString {
        buildContent {
            preferences.entries.forEach { entry ->
                when (entry) {
                    is BooleanEntry -> tag(BOOLEAN) { attribute(name = entry.name, value = entry.value.toString()) }
                    is FloatEntry -> tag(FLOAT) { attribute(name = entry.name, value = entry.value.toString()) }
                    is LongEntry -> tag(LONG) { attribute(name = entry.name, value = entry.value.toString()) }
                    is IntEntry -> tag(INT) { attribute(name = entry.name, value = entry.value.toString()) }

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
        }.also { append(it.toString()) }
    }

    private fun XmlSerializer.attribute(name: String, value: String) {
        attribute(null, NAME, name)
        attribute(null, VALUE, value)
    }

    private fun XmlSerializer.tag(tag: String, block: XmlSerializer.() -> Unit) {
        startTag(null, tag)
        block()
        endTag(null, tag)
    }

    private fun buildContent(block: XmlSerializer.() -> Unit): OutputStream = ByteArrayOutputStream().also {
        with(XmlPullParserFactory.newInstance().newSerializer()) {
            setOutput(it, ENCODING)
            setFeature(INDENTATION_FEATURE, true)
            startDocument(ENCODING, true)
            startTag(null, ROOT)
            block()
            endDocument()
        }
    }

    companion object {
        const val INDENTATION_FEATURE = "http://xmlpull.org/v1/doc/features.html#indent-output"
        const val ENCODING = "utf-8"
        const val PATTERN = "***"
        const val VALUE = "value"
        const val NAME = "name"
        const val SEP = "_"
    }
}
