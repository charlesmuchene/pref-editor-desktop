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

import com.charlesmuchene.prefeditor.data.Preferences
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor

class AndroidPreferencesUseCase(private val codec: AndroidPreferencesCodec, private val editor: PreferenceEditor) {

    private lateinit var preferences: Preferences

    fun writePreferences(entries: List<UIEntry>) {
        val edits = entries.filter { it.state !is EntryState.None }
        val content = codec.encode(edits = edits, existing = preferences.entries)
    }
}