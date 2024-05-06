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

package com.charlesmuchene.prefeditor.ui.filter

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.models.ItemFilter
import com.charlesmuchene.prefeditor.ui.APP_HALF_SPACING
import org.jetbrains.jewel.ui.component.CheckboxRow

@Composable
fun FilterComponent(
    placeholder: String,
    onFilter: (ItemFilter) -> Unit,
    modifier: Modifier = Modifier,
) {
    var checked by remember { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    val currentOnFilter by rememberUpdatedState(onFilter)

    LaunchedEffect(checked, text) {
        currentOnFilter(ItemFilter(text = text, starred = checked))
    }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        FilterTextField(placeholder = placeholder, onClear = {
            text = ""
            onFilter(ItemFilter(text = "", starred = checked))
        }, filtered = { text = it })
        Spacer(modifier = Modifier.width(APP_HALF_SPACING))
        CheckboxRow(
            onCheckedChange = { checked = !checked },
            modifier = Modifier.pointerOnHover(),
            checked = checked,
            text = "Starred",
        )
    }
}
