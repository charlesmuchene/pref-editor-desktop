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

package com.charlesmuchene.prefeditor.bridge

import com.charlesmuchene.prefeditor.bridge.BridgeStatus.Available
import com.charlesmuchene.prefeditor.bridge.BridgeStatus.Unavailable
import com.charlesmuchene.prefeditor.processor.Processor
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import java.io.File
import java.nio.file.Path
import kotlin.io.path.pathString

/**
 * Interface to the debug bridge
 */
class Bridge(private val processor: Processor = Processor()) {
    private suspend fun checkPrefs(path: Path?): BridgeStatus {
        return if (path == null) Unavailable else run(path.pathString)
    }

    private suspend fun checkSdkDir(): BridgeStatus {
        val path = sdkDirPath() ?: return Unavailable
        return run(path.pathString)
    }

    suspend fun checkBridge(executablePath: Path?): BridgeStatus =
        coroutineScope {
            awaitAll(async { checkPrefs(executablePath) }, async { checkSdkDir() })
                .firstOrNull { it is Available } ?: Unavailable
        }

    private fun sdkDirPath(): Path? {
        val username = System.getProperty("user.name") ?: return null
        // TODO Add support for win + linux
        return Path.of(File.separator, "Users", username, "Library", "Android", "sdk", "platform-tools", "adb")
    }

    private suspend fun run(command: String): BridgeStatus {
        val result =
            processor.run(listOf(command)) {
                redirectOutput(ProcessBuilder.Redirect.DISCARD)
                redirectError(ProcessBuilder.Redirect.DISCARD)
            }
        return if (result.isSuccess(alternateSuccessCode = 1)) Available(command) else Unavailable
    }
}
