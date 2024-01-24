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

package com.charlesmuchene.prefedit.screens.preferences.editor.entries

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefedit.screens.preferences.editor.entries.rows.*

val componentSpacing = 8.dp
const val valueComponentWeight = 0.7f
const val nameComponentWeight = 0.3f

@Composable
fun PrimitiveEntry(entry: Entry, modifier: Modifier = Modifier, viewModel: EditorViewModel) {
    // TODO Wrap name to 2 lines, overflow -- clip?
    when (entry) {
        is BooleanEntry -> BooleanEntryRow(modifier = modifier, entry = entry, viewModel = viewModel)
        is FloatEntry -> FloatEntryRow(modifier = modifier, entry = entry, viewModel = viewModel)
        is IntEntry -> IntEntryRow(modifier = modifier, entry = entry, viewModel = viewModel)
        is LongEntry -> LongEntryRow(modifier = modifier, entry = entry, viewModel = viewModel)
        is StringEntry -> StringEntryRow(modifier = modifier, entry = entry, viewModel = viewModel)
        else -> Unit
    }
}