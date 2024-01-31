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

class PreferenceEditor(
    private val context: CoroutineContext = Dispatchers.IO,
    private val processor: Processor = Processor(),
) {

    suspend fun edit(edit: Edit, path: Path): String = withContext(context) {
        when (edit) {
            is Edit.Add -> add(edit = edit, path = path, processor = processor)
            is Edit.Change -> change(edit = edit, path = path, processor = processor)
            is Edit.Delete -> delete(edit = edit, path = path, processor = processor)
        }
    }

    private suspend fun add(edit: Edit.Add, path: Path, processor: Processor): String {
        val escaped = "\\"
        val command = "sh edit.sh <$escaped/${Tags.ROOT}> $path".split(" ")
        return processor.run(command) { environment()[CONTENT] = edit.content }
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