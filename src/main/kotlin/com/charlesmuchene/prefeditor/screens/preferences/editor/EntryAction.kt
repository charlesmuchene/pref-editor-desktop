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
import org.jetbrains.jewel.ui.component.IconButton

@Composable
fun EntryAction(uiEntry: UIEntry, modifier: Modifier = Modifier, onEntryAction: (EntryAction) -> Unit) {
    val isDeletable = uiEntry.state !is EntryState.Deleted
    val isResettable = uiEntry.state !is EntryState.None

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val resetPainter by rememberIconPainter(name = "reset@24x24")
        val deletePainter by rememberIconPainter(name = "trash@24x24")
        IconButton(
            enabled = isResettable,
            modifier = Modifier.weight(.5f),
            onClick = { onEntryAction(EntryAction.Action.Reset(uiEntry.entry)) }
        ) { state ->
            Icon(
                painter = resetPainter,
                contentDescription = "Reset",
                tint = if (state.isEnabled) Color.Black else Color.LightGray,
                modifier = Modifier.pointerOnHover().size(24.dp).padding(4.dp),
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        IconButton(
            enabled = isDeletable,
            modifier = Modifier.weight(.5f),
            onClick = { onEntryAction(EntryAction.Action.Delete(uiEntry.entry)) }
        ) { state ->
            Icon(
                painter = deletePainter,
                contentDescription = "Delete",
                tint = if (state.isEnabled) Color.Black else Color.LightGray,
                modifier = Modifier.pointerOnHover().size(24.dp).padding(4.dp),
            )
        }
    }
}