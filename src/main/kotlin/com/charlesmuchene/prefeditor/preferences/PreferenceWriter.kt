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

import com.charlesmuchene.prefeditor.command.writes.EditorCommand
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
class PreferenceWriter(
    private val processor: Processor,
    private val command: EditorCommand,
) {

    /**
     * Perform the given edit
     *
     * @param edit [Edit] to make
     * @return Result of making the edit
     */
    suspend fun edit(edit: Edit): Result<String> = when (edit) {
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
    suspend fun edit(edits: List<Edit>): List<Result<String>> = buildList {
        add(adds = edits.filterIsInstance<Edit.Add>())
        delete(deletes = edits.filterIsInstance<Edit.Delete>())
        change(changes = edits.filterIsInstance<Edit.Change>())
    }

    private suspend fun MutableList<Result<String>>.add(adds: List<Edit.Add>) {
        adds.forEach { add -> add(add(edit = add)) }
    }

    private suspend fun add(edit: Edit.Add): Result<String> {
        val command = command.command(edit = edit)
        return processor.run(command) // TODO { environment()[CONTENT] = content }
    }

    private suspend fun MutableList<Result<String>>.delete(deletes: List<Edit.Delete>) {
        deletes.forEach { edit -> add(delete(edit = edit)) }
    }

    private suspend fun delete(edit: Edit.Delete): Result<String> {
        val command = command.command(edit = edit)
        return processor.run(command)
    }

    private suspend fun MutableList<Result<String>>.change(changes: List<Edit.Change>) {
        changes.forEach { edit -> add(change(edit = edit)) }
    }

    private suspend fun change(edit: Edit.Change): Result<String> {
        val command = command.command(edit = edit)
        return processor.run(command)
    }
}