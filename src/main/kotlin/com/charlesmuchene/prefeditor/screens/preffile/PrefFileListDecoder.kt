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

package com.charlesmuchene.prefeditor.screens.preffile

import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.PrefFiles
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.Files
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.NoFiles
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.NonDebuggable
import kotlinx.coroutines.yield

class PrefFileListDecoder {
    suspend fun decode(contents: List<String>): PrefFileResult {
        val results = contents.map { process(it) }.toSet()
        val files = results.filterIsInstance<Files>().flatMap(Files::files)
        return when {
            files.isNotEmpty() -> Files(files)
            results.contains(NonDebuggable) -> NonDebuggable
            else -> NoFiles
        }
    }

    private suspend fun process(content: String): PrefFileResult {
        val sanitizedContent = content.trim()
        return when {
            sanitizedContent.isBlank() || sanitizedContent == EMPTY_PREFS || sanitizedContent == EMPTY_FILES -> NoFiles
            sanitizedContent.startsWith(NON_DEBUGGABLE) -> NonDebuggable
            else ->
                Files(
                    buildList {
                        sanitizedContent
                            .lineSequence()
                            .forEach { name ->
                                add(PrefFile(name = name, type = type(name)))
                                yield()
                            }
                    },
                )
        }
    }

    private fun type(name: String) =
        if (DATASTORE_FILENAME_SUFFIX.matches(name)) PrefFile.Type.DATA_STORE else PrefFile.Type.KEY_VALUE

    sealed interface PrefFileResult {
        data object NoFiles : PrefFileResult

        data object NonDebuggable : PrefFileResult

        data class Files(val files: PrefFiles) : PrefFileResult
    }

    private companion object {
        private val DATASTORE_FILENAME_SUFFIX = ".*pb".toRegex()
        const val EMPTY_PREFS = "ls: shared_prefs: No such file or directory"
        const val EMPTY_FILES = "ls: files: No such file or directory"
        const val NON_DEBUGGABLE = "run-as: package not debuggable:"
    }
}
