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

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.datastore.preferences.BooleanPreference
import com.charlesmuchene.datastore.preferences.FloatPreference
import com.charlesmuchene.datastore.preferences.IntPreference
import com.charlesmuchene.datastore.preferences.LongPreference
import com.charlesmuchene.datastore.preferences.StringPreference
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows.BooleanPreferenceRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows.FloatPreferenceRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows.IntPreferenceRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows.LongPreferenceRow
import com.charlesmuchene.prefeditor.screens.preferences.device.editor.rows.StringPreferenceRow

val componentSpacing = 8.dp
const val VALUE_COMPONENT_WEIGHT = 0.65f
const val NAME_COMPONENT_WEIGHT = 0.25f
const val ACTION_COMPONENT_WEIGHT = 0.1f

@Composable
fun PrimitivePreference(
    preference: UIPreference,
    viewModel: EditorViewModel,
    modifier: Modifier = Modifier,
) {
    // TODO Wrap name to 2 lines, overflow -- clip?
    when (preference.preference) {
        is BooleanPreference ->
            BooleanPreferenceRow(
                modifier = modifier,
                uiPreference = preference,
                viewModel = viewModel,
            )

        is FloatPreference -> FloatPreferenceRow(modifier = modifier, preference = preference, viewModel = viewModel)
        is IntPreference -> IntPreferenceRow(modifier = modifier, preference = preference, viewModel = viewModel)
        is LongPreference -> LongPreferenceRow(modifier = modifier, uiPreference = preference, viewModel = viewModel)
        is StringPreference ->
            StringPreferenceRow(modifier = modifier, uiPreference = preference, viewModel = viewModel)
        else -> Unit
    }
}
