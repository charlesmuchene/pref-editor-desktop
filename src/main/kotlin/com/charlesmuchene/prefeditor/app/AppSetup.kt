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
): AppStatus {
    val codec = PreferencesCodec()
    EditorFiles.initialize(codec = codec, appPathOverride = pathOverride)
    val path = EditorFiles.preferencesPath(appPathOverride = pathOverride?.resolve(EditorFiles.ROOT_DIR))
    val command = DesktopWriteCommand(path = path.pathString)
    val editor = PreferenceWriter(command = command, processor = processor)
    val preferences = AppPreferences(codec = codec, writer = editor, path = path).apply { initialize() }
    val appState = AppState(preferences = preferences)

    val bridgeStatus =
        Bridge(processor = processor)
            .checkBridge(executablePath = preferences.executable.path.value)

    return when (bridgeStatus) {
        is BridgeStatus.Available -> {
            appState.executable = bridgeStatus.path
            AppStatus.Ready(state = appState)
        }

        BridgeStatus.Unavailable -> AppStatus.NoBridge(state = appState)
    }
}
