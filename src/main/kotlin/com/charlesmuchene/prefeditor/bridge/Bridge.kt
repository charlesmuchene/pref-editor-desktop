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
import com.charlesmuchene.prefeditor.command.Command
import com.charlesmuchene.prefeditor.command.ReadCommand
import com.charlesmuchene.prefeditor.command.WriteCommand
import com.charlesmuchene.prefeditor.files.PrefEditorFiles.appPath
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.pathString

/**
 * Interface to the debug bridge
 *
 * Current implementation expects the bridge to be on the system `PATH`
 *
 * @param context [CoroutineContext] for all bridge operations
 */
class Bridge(private val context: CoroutineContext = Dispatchers.IO + CoroutineName(name = "Bridge")) {

    /**
     * Execute the given read command
     *
     * @param command [ReadCommand] to execute
     * @return [Result] of parsing [T]
     */
    suspend fun <T> execute(command: ReadCommand<T>): Result<T> = withContext(context) {
        createProcess(command = command)
    }

    /**
     * Execute the given write command
     *
     * @param command [WriteCommand] to execute
     * @return [Result] of parsing [T]
     */
    suspend fun <T> execute(command: WriteCommand<T>): Result<T> = withContext(context) {
        createProcess(command) { environment()[CONTENT] = command.content }
    }

    private fun <T> createProcess(command: Command<T>, config: ProcessBuilder.() -> Unit = {}): Result<T> =
        with(ProcessBuilder(command.command.split(DELIMITER))) {
            environment()[PATH] += ":${appPath().pathString}"
            redirectErrorStream(true)
            config()
            execute(command::execute)
        }

    private fun <T> ProcessBuilder.execute(block: (BufferedSource) -> T): Result<T> {
        val process = try {
            start()
        } catch (t: Throwable) {
            return Result.failure(t)
        }
        return process
            .inputStream
            .source()
            .buffer()
            .use { source ->
                try {
                    Result.success(block(source))
                } catch (t: Throwable) {
                    Result.failure(t)
                }
            }
    }

    companion object {
        private const val ADB = "adb"
        private const val PATH = "PATH"
        private const val DELIMITER = " "
        private const val CONTENT = "PREF_EDITOR_CONTENT"

        suspend fun checkBridge(context: CoroutineContext = Dispatchers.IO): BridgeStatus = withContext(context) {
            val builder = ProcessBuilder()
                .command(ADB)
                .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                .redirectError(ProcessBuilder.Redirect.DISCARD)

            try {
                builder.start()
                Available
            } catch (_: IOException) {
                Unavailable
            }
        }
    }
}