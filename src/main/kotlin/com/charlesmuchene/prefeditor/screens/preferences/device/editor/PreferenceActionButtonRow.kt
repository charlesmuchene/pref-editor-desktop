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

package com.charlesmuchene.prefeditor.screens.preferences.device.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import com.charlesmuchene.prefeditor.extensions.pointerOnHover
import com.charlesmuchene.prefeditor.extensions.rememberIconPainter
import com.charlesmuchene.prefeditor.providers.LocalAppState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceAction.Delete
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceAction.Reset
import com.charlesmuchene.prefeditor.ui.APP_ICON_BUTTON_SIZE
import com.charlesmuchene.prefeditor.ui.APP_SPACING
import com.charlesmuchene.prefeditor.ui.theme.mustard
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Icon
import org.jetbrains.jewel.ui.component.IconButton

@Composable
fun PreferenceActionButtonRow(
    preference: UIPreference,
    modifier: Modifier = Modifier,
    onPreferenceAction: (PreferenceAction) -> Unit,
) {
    val isDeletable = preference.state !is PreferenceState.Deleted
    val isResettable = preference.state !is PreferenceState.None

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        val resetPainter by rememberIconPainter(name = "reset")
        val deletePainter by rememberIconPainter(name = "trash")
        IconButton(
            enabled = isResettable,
            modifier = Modifier.size(APP_ICON_BUTTON_SIZE).clip(CircleShape),
            onClick = { onPreferenceAction(Reset(preference.preference)) },
        ) { state ->
            val tint = if (LocalAppState.current.theme.isDark()) mustard else JewelTheme.contentColor
            Icon(
                painter = resetPainter,
                contentDescription = "Reset",
                tint = if (state.isEnabled) tint else Color.LightGray,
                modifier = Modifier.pointerOnHover().size(APP_SPACING),
            )
        }
        IconButton(
            enabled = isDeletable,
            modifier = Modifier.size(APP_ICON_BUTTON_SIZE).clip(CircleShape),
            onClick = { onPreferenceAction(Delete(preference.preference)) },
        ) { state ->
            Icon(
                painter = deletePainter,
                contentDescription = "Delete",
                tint = if (state.isEnabled) Color.Red else Color.LightGray,
                modifier = Modifier.pointerOnHover().size(APP_SPACING),
            )
        }
    }
}
