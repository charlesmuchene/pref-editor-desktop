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

import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.PrefFiles
import okio.BufferedSource

class PrefFilesParser : Parser<PrefFiles> {

    override fun parse(source: BufferedSource): PrefFiles = buildList {
        while (true) {
            val line = source.readUtf8Line() ?: break
            parseFile(line)?.let(::add)
        }
    }

    private fun parseFile(line: String): PrefFile? {
        if (line.isBlank()) return null
        if (line == NO_PREFS || line == NO_FILES || line.startsWith(NON_DEBUGGABLE)) return null
        return PrefFile(name = line, type = PrefFile.Type.KEY_VALUE)
    }

    private companion object {
        private const val NO_PREFS = "ls: shared_prefs: No such file or directory"
        private const val NO_FILES = "ls: files: No such file or directory"
        private const val NON_DEBUGGABLE = "run-as: package not debuggable:"
    }
}