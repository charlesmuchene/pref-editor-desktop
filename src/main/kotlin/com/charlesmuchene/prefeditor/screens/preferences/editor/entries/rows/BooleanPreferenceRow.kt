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

package com.charlesmuchene.prefeditor.screens.preferences.editor.entries.rows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefeditor.data.BooleanPreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.editor.PreferenceAction
import com.charlesmuchene.prefeditor.screens.preferences.editor.UIPreference
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.ACTION_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.NAME_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.VALUE_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.editor.entries.componentSpacing
import com.charlesmuchene.prefeditor.ui.theme.Typography
import org.jetbrains.jewel.ui.component.RadioButtonRow
import org.jetbrains.jewel.ui.component.Text

@Composable
fun BooleanPreferenceRow(uiPreference: UIPreference, viewModel: EditorViewModel, modifier: Modifier = Modifier) {
    val preference = uiPreference.preference as? BooleanPreference ?: return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing)
    ) {
        Text(text = preference.name, style = Typography.secondary, modifier = Modifier.weight(NAME_COMPONENT_WEIGHT))

        var localPreference by remember(uiPreference) { mutableStateOf(uiPreference) }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(VALUE_COMPONENT_WEIGHT)) {
            val trueString by remember() { mutableStateOf(true.toString()) }
            val falseString by remember() { mutableStateOf(false.toString()) }
            val outline by remember(localPreference) { mutableStateOf(viewModel.outline(preference = localPreference.preference)) }
            var isTrue by remember(localPreference) { mutableStateOf(localPreference.preference.value) }

            RadioButtonRow(
                selected = isTrue.toBooleanStrict(),
                outline = outline,
                onClick = {
                    isTrue = trueString
                    val change = PreferenceAction.Change(preference = localPreference.preference, change = trueString)
                    localPreference = viewModel.preferenceAction(change)
                },
            ) { Text(text = "True") }

            RadioButtonRow(
                selected = !isTrue.toBooleanStrict(),
                outline = outline,
                onClick = {
                    isTrue = falseString
                    val change = PreferenceAction.Change(preference = localPreference.preference, change = falseString)
                    localPreference = viewModel.preferenceAction(change)
                },
            ) { Text(text = "False") }
        }

        PreferenceAction(
            onPreferenceAction = { localPreference = viewModel.preferenceAction(it) },
            modifier = Modifier.weight(ACTION_COMPONENT_WEIGHT),
            preference = localPreference,
        )
    }
}