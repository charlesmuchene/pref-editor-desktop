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
        add(adds = adds, path = path)

        val deletes = edits.filterIsInstance<Edit.Delete>()
        delete(edits = deletes, path = path, processor = processor)
    }

    private suspend fun PreferenceEditor.add(adds: List<Edit.Add>, path: Path): String {
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
        val tag = "</${Tags.ROOT}>".escaped()
        val command = "sh add.sh $tag $path".split(DELIMITER)
        return processor.run(command) { environment()[CONTENT] = content }
    }

    private suspend fun delete(edit: Edit.Delete, path: Path, processor: Processor): String {
        return delete(content = edit.matcher, path = path, processor = processor)
    }

    private suspend fun delete(edits: List<Edit.Delete>, path: Path, processor: Processor): String = buildString {
        edits.forEach { edit ->
            append(delete(edit = edit, path = path, processor = processor))
            append(System.lineSeparator())
        }
    }

    private suspend fun delete(content: String, path: Path, processor: Processor): String {
        val command = buildList {
            add("sh")
            add("delete.sh")
            add(content.escaped())
            add(path.toString())
        }
        return processor.run(command)
    }

    private suspend fun change(edit: Edit.Change, path: Path, processor: Processor): String {
        val matcher = edit.matcher.escaped()
        val content = edit.content.escaped()
        val command = buildList {
            add("sh")
            add("change.sh")
            add(matcher)
            add(content)
            add(path.toString())
        }
        return processor.run(command)
    }

    fun String.escaped(): String = replace(oldValue = "/", newValue = "\\/")
        .replace(oldValue = "\"", newValue = "\\\"")

    companion object {
        private const val DELIMITER = " "
        private const val CONTENT = "PREF_EDITOR_CONTENT"
    }
}