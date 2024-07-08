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

package com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.ACTION_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.NAME_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceAction.Change
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceActionButtonRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState.Deleted
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.VALUE_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.componentSpacing
import org.jetbrains.jewel.ui.component.RadioButtonRow
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.Typography

@Composable
fun BooleanPreferenceRow(
    uiPreference: UIPreference,
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier,
) {
    val preference = uiPreference.preference as? BooleanPreference ?: return

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing),
    ) {
        var localPreference by remember(uiPreference) { mutableStateOf(uiPreference) }
        val trueString by remember { mutableStateOf(true.toString()) }
        val falseString by remember { mutableStateOf(false.toString()) }

        val outline by remember(localPreference) {
            mutableStateOf(viewModel.outline(preference = localPreference.preference))
        }
        var isTrue by remember(localPreference) {
            mutableStateOf(localPreference.preference.value)
        }
        val isEnabled by remember(localPreference) {
            mutableStateOf(localPreference.state !is Deleted)
        }
        val decoration by remember(localPreference) {
            mutableStateOf(if (localPreference.state is Deleted) TextDecoration.LineThrough else null)
        }

        Text(
            text = preference.key,
            textDecoration = decoration,
            style = Typography.h3TextStyle(),
            modifier = Modifier.weight(NAME_COMPONENT_WEIGHT),
        )

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.weight(VALUE_COMPONENT_WEIGHT)) {
            RadioButtonRow(
                selected = isTrue.toBooleanStrict(),
                enabled = isEnabled,
                outline = outline,
                onClick = {
                    isTrue = trueString
                    val change = Change(preference = localPreference.preference, change = trueString)
                    localPreference = viewModel.preferenceAction(change)
                },
            ) { Text(text = "True", textDecoration = decoration) }

            RadioButtonRow(
                selected = !isTrue.toBooleanStrict(),
                enabled = isEnabled,
                outline = outline,
                onClick = {
                    isTrue = falseString
                    val change = Change(preference = localPreference.preference, change = falseString)
                    localPreference = viewModel.preferenceAction(change)
                },
            ) { Text(text = "False", textDecoration = decoration) }
        }

        PreferenceActionButtonRow(
            onPreferenceAction = { localPreference = viewModel.preferenceAction(it) },
            modifier = Modifier.weight(ACTION_COMPONENT_WEIGHT),
            preference = localPreference,
        )
    }
}
