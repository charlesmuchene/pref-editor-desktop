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

package com.charlesmuchene.prefeditor.screens.preferences.editor.entries

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.SetPreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.UIPreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.rows.SetPreferenceRow
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider
import org.jetbrains.jewel.ui.component.Text

@OptIn(ExperimentalFoundationApi::class)
fun LazyListScope.setPreferenceSection(preferences: List<UIPreference>, viewModel: EditorViewModel) {
    item {
        Divider(
            orientation = Orientation.Horizontal,
            color = Color.LightGray.copy(alpha = 0.5f),
            modifier = Modifier.padding(vertical = 8.dp)
        )
    }

    stickyHeader {
        Row(
            modifier = Modifier.padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "String sets", style = Typography.heading)
            Text(
                text = "(editing not yet supported)",
                style = Typography.secondary,
                color = JewelTheme.contentColor,
            )
        }
    }

    items(items = preferences.mapNotNull { it.preference as? SetPreference }, key = SetPreference::name) { preference ->
        SetPreferenceRow(
            preference = preference,
            viewModel = viewModel,
            modifier = Modifier.padding(end = 18.dp).fillMaxWidth().height((64 * preference.entries.size).dp),
        )
    }
}