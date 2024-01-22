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

package com.charlesmuchene.prefedit.screens.prefs

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.data.Preferences
import com.charlesmuchene.prefedit.resources.PrefKey
import com.charlesmuchene.prefedit.ui.SingleText

@Composable
fun Editor(preferences: Preferences, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val viewModel = remember { EditorViewModel(preferences = preferences, scope = scope) }
    println(viewModel)
    SingleText(key = PrefKey.PrefTitle, modifier = modifier)
}