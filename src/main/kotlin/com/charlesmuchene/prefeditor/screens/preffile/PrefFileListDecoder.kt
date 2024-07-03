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
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.EmptyFiles
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.EmptyPrefs
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.Files
import com.charlesmuchene.prefeditor.screens.preffile.PrefFileListDecoder.PrefFileResult.NonDebuggable
import kotlinx.coroutines.yield

class PrefFileListDecoder {
    suspend fun decode(content: String): PrefFileResult {
        val sanitizedContent = content.trim()
        return when {
            sanitizedContent.isBlank() -> EmptyPrefs
            sanitizedContent == EMPTY_FILES -> EmptyFiles
            sanitizedContent == EMPTY_PREFS -> EmptyPrefs
            sanitizedContent.startsWith(NON_DEBUGGABLE) -> NonDebuggable
            else ->
                Files(
                    buildList {
                        sanitizedContent.lineSequence().forEach { name ->
                            yield()
                            add(PrefFile(name = name, type = PrefFile.Type.KEY_VALUE))
                        }
                    },
                )
        }
    }

    sealed interface PrefFileResult {
        data object EmptyFiles : PrefFileResult

        data object EmptyPrefs : PrefFileResult

        data object NonDebuggable : PrefFileResult

        data class Files(val files: PrefFiles) : PrefFileResult
    }

    private companion object {
        const val EMPTY_PREFS = "ls: shared_prefs: No such file or directory"
        const val EMPTY_FILES = "ls: files: No such file or directory"
        const val NON_DEBUGGABLE = "run-as: package not debuggable:"
    }
}
