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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextDecoration
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.ACTION_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.EditorViewModel
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.NAME_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceAction
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceActionButtonRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.PreferenceState
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.UIPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.VALUE_COMPONENT_WEIGHT
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.componentSpacing
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField
import org.jetbrains.jewel.ui.component.Typography

@Composable
fun PrimitivePreferenceRow(
    preference: UIPreference,
    viewModel: EditorViewModel,
    keyboardType: KeyboardType,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(componentSpacing),
    ) {
        var localPreference by remember(preference) { mutableStateOf(preference) }

        val outline by remember(localPreference) {
            mutableStateOf(viewModel.outline(preference = localPreference.preference))
        }
        val isEnabled by remember(localPreference) {
            mutableStateOf(localPreference.state !is PreferenceState.Deleted)
        }
        val decoration by remember(localPreference) {
            mutableStateOf(if (localPreference.state is PreferenceState.Deleted) TextDecoration.LineThrough else null)
        }
        Text(
            textDecoration = decoration,
            style = Typography.h3TextStyle(),
            text = preference.preference.name,
            modifier = Modifier.weight(NAME_COMPONENT_WEIGHT),
        )

        // Adapted from Jewel, all so that we can strike through text :sad:
        var textFieldValueState by remember {
            mutableStateOf(TextFieldValue(text = localPreference.preference.value))
        }
        val textFieldValue = textFieldValueState.copy(text = localPreference.preference.value)
        var lastTextValue by remember(localPreference.preference.value) {
            mutableStateOf(localPreference.preference.value)
        }

        TextField(
            textStyle = JewelTheme.textStyle.copy(textDecoration = decoration),
            outline = outline,
            enabled = isEnabled,
            value = textFieldValue,
            modifier = Modifier.weight(VALUE_COMPONENT_WEIGHT),
            placeholder = { Text(text = preference.preference.value) },
            keyboardOptions = KeyboardOptions(autoCorrect = false, keyboardType = keyboardType),
            onValueChange = { valueState ->
                textFieldValueState = valueState
                val stringChangedSinceLastInvocation = lastTextValue != valueState.text
                lastTextValue = valueState.text
                if (stringChangedSinceLastInvocation) {
                    val change =
                        PreferenceAction.Change(preference = localPreference.preference, change = valueState.text)
                    localPreference = viewModel.preferenceAction(change)
                }
            },
        )

        PreferenceActionButtonRow(
            onPreferenceAction = { localPreference = viewModel.preferenceAction(it) },
            modifier = Modifier.weight(ACTION_COMPONENT_WEIGHT),
            preference = localPreference,
        )
    }
}
