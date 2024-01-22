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

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.data.*
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.PrefKey
import com.charlesmuchene.prefedit.ui.Listing
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.theme.PrefEditTextStyle
import org.jetbrains.jewel.ui.component.Text

@Composable
fun Editor(preferences: Preferences, modifier: Modifier = Modifier) {
    val text = LocalBundle.current[PrefKey.PrefTitle]
    val scope = rememberCoroutineScope()
    val viewModel = remember { EditorViewModel(preferences = preferences, scope = scope) }

    Listing(header = text, modifier = modifier) {
        // TODO Find suitable key: Make 'Entry' sealed class?
        items(items = preferences.entries) { entry ->
            PrefEntry(entry = entry, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
private fun PrefEntry(entry: Entry, modifier: Modifier = Modifier) {
    when (entry) {
        is BooleanEntry -> BooleanEntryRow(modifier = modifier, entry = entry)
        is FloatEntry -> FloatEntryRow(modifier = modifier, entry = entry)
        is IntEntry -> IntEntryRow(modifier = modifier, entry = entry)
        is LongEntry -> LongEntryRow(modifier = modifier, entry = entry)
        is SetEntry -> SetEntryRow(modifier = modifier, entry = entry)
        is StringEntry -> StringEntryRow(modifier = modifier, entry = entry)
    }
}

@Composable
fun BooleanEntryRow(entry: BooleanEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.value.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun FloatEntryRow(entry: FloatEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.value.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun IntEntryRow(entry: IntEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.value.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun LongEntryRow(entry: LongEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.value.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun SetEntryRow(entry: SetEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.entries.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun StringEntryRow(entry: StringEntry, modifier: Modifier = Modifier) {
    Row(modifier = modifier) {
        Text(text = entry.name, style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.3f))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = entry.value.toString(), style = PrefEditTextStyle.primary, modifier = Modifier.weight(0.7f))
    }
}