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

import com.charlesmuchene.prefeditor.command.ListPrefFiles.PrefFilesResult
import com.charlesmuchene.prefeditor.data.PrefFile
import okio.BufferedSource

class PrefFilesParser : Parser<PrefFilesResult> {

    // FIXME: Hard to read, refactor
    override fun parse(source: BufferedSource): PrefFilesResult {
        val line = source.readUtf8Line() ?: return PrefFilesResult.EmptyPrefs
        when {
            line.isBlank() -> Unit
            line == EMPTY_FILES -> return PrefFilesResult.EmptyFiles
            line == EMPTY_PREFS -> return PrefFilesResult.EmptyPrefs
            line.startsWith(NON_DEBUGGABLE) -> return PrefFilesResult.NonDebuggable
            else -> {
                val files = buildList {
                    add(PrefFile(name = line, type = PrefFile.Type.KEY_VALUE))
                    while (true) {
                        val name = source.readUtf8Line() ?: break
                        add(PrefFile(name = name, type = PrefFile.Type.KEY_VALUE))
                    }
                }
                return PrefFilesResult.Files(files)
            }
        }
        return PrefFilesResult.EmptyPrefs
    }

    private companion object {
        private const val EMPTY_PREFS = "ls: shared_prefs: No such file or directory"
        private const val EMPTY_FILES = "ls: files: No such file or directory"
        private const val NON_DEBUGGABLE = "run-as: package not debuggable:"
    }
}