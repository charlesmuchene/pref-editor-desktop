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

import com.charlesmuchene.prefeditor.command.EditorCommand
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.processor.Processor

/**
 * Edit a given preference.
 *
 * There are 3 edit operations supported:
 *  - [Edit.Add]
 *  - [Edit.Change]
 *  - [Edit.Delete]
 */
class PreferenceEditor(
    private val command: EditorCommand,
    private val processor: Processor = Processor(),
) {

    /**
     * Perform the given edit
     *
     * @param edit [Edit] to make
     * @return Result of making the edit
     */
    suspend fun edit(edit: Edit): String = when (edit) {
        is Edit.Add -> add(edit = edit)
        is Edit.Change -> change(edit = edit)
        is Edit.Delete -> delete(edit = edit)
    }

    /**
     * Perform a batch edit
     *
     * @param edits A [List] of [Edit]s to make
     * @return Result of batch editing
     */
    suspend fun edit(edits: List<Edit>): String = buildString {
        append(add(adds = edits.filterIsInstance<Edit.Add>()))
        append(System.lineSeparator())
        append(delete(deletes = edits.filterIsInstance<Edit.Delete>()))
        append(System.lineSeparator())
        append(change(changes = edits.filterIsInstance<Edit.Change>()))
    }

    private suspend fun add(adds: List<Edit.Add>): String = buildString {
        adds.forEach { add ->
            append(add(edit = add))
            append(System.lineSeparator())
        }
    }

    private suspend fun add(edit: Edit.Add): String {
        val command = command.command(edit = edit)
        return processor.run(command) // TODO { environment()[CONTENT] = content }
    }

    private suspend fun delete(deletes: List<Edit.Delete>): String = buildString {
        deletes.forEach { edit ->
            append(delete(edit = edit))
            append(System.lineSeparator())
        }
    }

    private suspend fun delete(edit: Edit.Delete): String {
        val command = command.command(edit = edit)
        return processor.run(command)
    }

    private suspend fun change(changes: List<Edit.Change>): String = buildString {
        changes.forEach { edit ->
            append(change(edit = edit))
            append(System.lineSeparator())
        }
    }

    private suspend fun change(edit: Edit.Change): String {
        val command = command.command(edit = edit)
        return processor.run(command)
    }
}