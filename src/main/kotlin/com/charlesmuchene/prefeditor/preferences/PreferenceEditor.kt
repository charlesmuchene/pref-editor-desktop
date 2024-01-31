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

package com.charlesmuchene.prefeditor.preferences

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.data.Tags
import com.charlesmuchene.prefeditor.processor.Processor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Path
import kotlin.coroutines.CoroutineContext

/**
 * Edit a given preference.
 *
 * There are 3 edit operations supported:
 *  - [Edit.Add]
 *  - [Edit.Change]
 *  - [Edit.Delete]
 */
class PreferenceEditor(
    private val context: CoroutineContext = Dispatchers.IO,
    private val processor: Processor = Processor(),
) {

    /**
     * Perform the given edit
     *
     * @param edit [Edit] to make
     * @param path [Path] to the edit file
     * @return Result of making the edit
     */
    suspend fun edit(edit: Edit, path: Path): String = withContext(context) {
        when (edit) {
            is Edit.Add -> add(edit = edit, path = path, processor = processor)
            is Edit.Change -> change(edit = edit, path = path, processor = processor)
            is Edit.Delete -> delete(edit = edit, path = path, processor = processor)
        }
    }

    /**
     * Perform a batch edit
     *
     * @param edits A [List] of [Edit]s to make
     * @param path [Path] to the edit file
     * @return Result of batch editing
     */
    suspend fun edit(edits: List<Edit>, path: Path): String = withContext(context) {
        val adds = edits.filterIsInstance<Edit.Add>()
        batchAdd(adds, path)
    }

    private suspend fun PreferenceEditor.batchAdd(adds: List<Edit.Add>, path: Path): String {
        val content = buildString {
            adds.forEach { add ->
                append(add.content)
                append(System.lineSeparator())
            }
        }
        return add(content = content, path = path, processor = processor)
    }

    private suspend fun add(edit: Edit.Add, path: Path, processor: Processor): String {
        return add(content = edit.content, path = path, processor = processor)
    }

    private suspend fun add(content: String, path: Path, processor: Processor): String {
        val escaped = "\\"
        val command = "sh edit.sh <$escaped/${Tags.ROOT}> $path".split(" ")
        return processor.run(command) { environment()[CONTENT] = content }
    }

    private suspend fun delete(edit: Edit.Delete, path: Path, processor: Processor): String {
        return "TODO - delete"
    }

    private suspend fun change(edit: Edit.Change, path: Path, processor: Processor): String {
        return "TODO - change"
    }

    companion object {
        private const val CONTENT = "PREF_EDITOR_CONTENT"
    }
}