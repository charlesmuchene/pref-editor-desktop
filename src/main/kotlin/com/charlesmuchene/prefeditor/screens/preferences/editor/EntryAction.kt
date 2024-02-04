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

package com.charlesmuchene.prefeditor.screens.preferences.editor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import org.jetbrains.jewel.ui.component.Icon

@Composable
fun EntryAction(uiEntry: UIEntry, modifier: Modifier = Modifier, onEntryAction: (EntryAction) -> Unit) {
    val isDeletable = uiEntry.state !is EntryState.Deleted
    val isResettable = uiEntry.state !is EntryState.None

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val reset by rememberIconPainter(name = "reset@24x24")
        val delete by rememberIconPainter(name = "trash@24x24")
        // TODO Icon button??
        Icon(
            painter = reset,
            contentDescription = "Reset",
            tint = if (isResettable) Color.Black else Color.LightGray,
            modifier = Modifier
                .pointerOnHover()
                .size(20.dp)
                .clickable { onEntryAction(EntryAction.Action.Reset(uiEntry.entry)) }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Icon(
            painter = delete,
            contentDescription = "Delete",
            tint = if (isDeletable) Color.Black else Color.LightGray,
            modifier = Modifier
                .pointerOnHover()
                .size(20.dp)
                .clickable { onEntryAction(EntryAction.Action.Delete(uiEntry.entry)) }
        )
    }
}