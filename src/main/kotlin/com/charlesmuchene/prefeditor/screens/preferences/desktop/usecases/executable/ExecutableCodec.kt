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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.executable

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.gobbleTag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceDecoder.Reader.skip
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import org.xmlpull.v1.XmlPullParser
import java.nio.file.Path
import kotlin.io.path.inputStream
import kotlin.io.path.pathString

class ExecutableCodec(private val codec: PreferencesCodec) {
    fun encode(path: Path): Edit = Edit.Add(content = codec.encode { tag(EXECUTABLE) { text(path.pathString) } })

    suspend fun decode(path: Path): Path? {
        var executablePath: Path? = null
        codec.decode(path.inputStream()) {
            when (name) {
                EXECUTABLE -> executablePath = parseExecutable()
                else -> skip()
            }
        }
        return executablePath
    }

    private fun XmlPullParser.parseExecutable(): Path? =
        gobbleTag(EXECUTABLE) {
            text?.let(Path::of)
        }

    companion object Tags {
        const val EXECUTABLE = "exec"
    }
}
