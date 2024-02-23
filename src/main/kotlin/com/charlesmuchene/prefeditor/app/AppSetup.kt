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

import com.charlesmuchene.prefeditor.bridge.Bridge
import com.charlesmuchene.prefeditor.bridge.BridgeStatus
import com.charlesmuchene.prefeditor.command.DesktopWriteCommand
import com.charlesmuchene.prefeditor.files.EditorFiles
import com.charlesmuchene.prefeditor.models.AppStatus
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import com.charlesmuchene.prefeditor.screens.preferences.codec.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.AppPreferences
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Set app up
 *
 * TODO Add some DI framework if these get crazy!
 */
suspend fun appSetup(
    pathOverride: Path? = null,
    processor: Processor = Processor(),
): AppStatus =
    coroutineScope {
        val appState =
            async {
                val codec = PreferencesCodec()
                EditorFiles.initialize(codec = codec, appPathOverride = pathOverride)
                val path = EditorFiles.preferencesPath(appPathOverride = pathOverride).pathString
                val command = DesktopWriteCommand(path = path)
                val editor = PreferenceWriter(command = command, processor = processor)
                val preferences = AppPreferences(codec = codec, editor = editor).apply { initialize() }
                AppState(preferences = preferences)
            }

        val bridgeStatus = async { Bridge(processor = processor).checkBridge() }

        val result = awaitAll(appState, bridgeStatus)
        val state = result[0] as AppState
        when (val status = result[1] as BridgeStatus) {
            BridgeStatus.Available -> AppStatus.Ready(state = state)
            BridgeStatus.Unavailable -> AppStatus.NoBridge(state = state, bridgeStatus = status)
        }
    }
