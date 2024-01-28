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

package com.charlesmuchene.prefedit.bridge

import com.charlesmuchene.prefedit.bridge.BridgeStatus.Available
import com.charlesmuchene.prefedit.bridge.BridgeStatus.Unavailable
import com.charlesmuchene.prefedit.command.Command
import com.charlesmuchene.prefedit.command.WriteCommand
import com.charlesmuchene.prefedit.files.PrefEditFiles.appPath
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.IOException
import kotlin.coroutines.CoroutineContext
import kotlin.io.path.pathString

class Bridge(private val context: CoroutineContext = Dispatchers.IO + CoroutineName(name = "Bridge")) {

    /**
     * Execute the given command
     *
     * @param command Command to execute
     * @return [Result] of [BufferedSource] for parsing
     */
    suspend fun <T> execute(command: Command<T>): Result<T> = withContext(context) {
        val tokens = command.command.split(DELIMITER)
        with(ProcessBuilder(tokens)) {
            redirectErrorStream(true)
            execute(command::execute)
        }
    }

    suspend fun <T> execute(command: WriteCommand<T>): Result<T> = withContext(context) {
        val tokens = command.command.split(DELIMITER)
        println(command.command)
        println(command.content)
        with(ProcessBuilder(tokens)) {
            environment()[PATH] += ":${appPath().pathString}"
            environment()[CONTENT] = command.content
            redirectErrorStream(true)
            execute(command::execute)
        }
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
        private const val CONTENT = "PREF_EDIT_CONTENT"

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