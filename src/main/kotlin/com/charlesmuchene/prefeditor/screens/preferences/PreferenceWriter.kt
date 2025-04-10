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

package com.charlesmuchene.prefeditor.screens.preferences

import com.charlesmuchene.prefeditor.command.WriteCommand
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.processor.ProcessorResult
import kotlinx.coroutines.yield

/**
 * Edit a given preference.
 *
 * The following operations are supported:
 *  - [Edit.Add]
 *  - [Edit.Change]
 *  - [Edit.Delete]
 *  - [Edit.Replace]
 */
class PreferenceWriter(private val processor: Processor, private val command: WriteCommand) {
    /**
     * Perform the given edit
     *
     * @param edit [Edit] to make
     * @return Result of making the edit
     */
    suspend fun edit(edit: Edit): ProcessorResult = processor.run(command.command(edit = edit))

    /**
     * Perform a collection of edits
     *
     * @param edits A [List] of [Edit]s to make
     * @return Result of batch editing
     */
    suspend fun edit(edits: List<Edit>): List<ProcessorResult> =
        buildList {
            edits.forEach {
                yield()
                add(edit(it))
            }
        }
}
