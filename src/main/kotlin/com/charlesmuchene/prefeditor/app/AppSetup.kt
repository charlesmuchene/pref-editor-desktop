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

package com.charlesmuchene.prefeditor.app

import com.charlesmuchene.prefeditor.command.editor.DesktopEditorCommand
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.preferences.AppPreferences
import com.charlesmuchene.prefeditor.preferences.PreferenceEditor
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Set app up
 *
 * TODO Add some DI framework if these get crazy!
 */
suspend fun appSetup(): AppState = withContext(Dispatchers.IO) {
    val codec = PreferencesCodec()
    EditorFiles.initialize(codec = codec)
    val path = EditorFiles.preferencesPath().toString()
    val command = DesktopEditorCommand(path = path)
    val editor = PreferenceEditor(command = command)
    val preferences = AppPreferences(codec = codec, editor = editor).apply { initialize() }
    AppState(preferences = preferences)
}