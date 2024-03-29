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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import org.xmlpull.v1.XmlPullParser
import java.nio.file.Path
import kotlin.io.path.inputStream

class ThemeCodec(private val codec: PreferencesCodec) {
    fun encode(theme: EditorTheme): Edit =
        Edit.Change(
            matcher = "<$THEME.*$",
            content = codec.encode { tag(THEME) { attrib(name = VALUE, value = theme.ordinal.toString()) } },
        )

    suspend fun decode(path: Path): EditorTheme? {
        var theme: EditorTheme? = null
        codec.decode(path.inputStream()) {
            when (name) {
                THEME -> theme = parseTheme()
                else -> skip()
            }
        }
        return theme
    }

    private fun XmlPullParser.parseTheme(): EditorTheme =
        gobbleTag(THEME) {
            val value = getAttributeValue(0).toInt()
            EditorTheme.entries[value]
        }

    companion object Tags {
        const val THEME = "theme"
        const val VALUE = "value"
    }
}
