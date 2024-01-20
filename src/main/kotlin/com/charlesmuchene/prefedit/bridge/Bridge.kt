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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.source
import java.io.IOException
import kotlin.coroutines.CoroutineContext

class Bridge(private val context: CoroutineContext = Dispatchers.IO + CoroutineName(name = "Bridge")) {

    /**
     * Execute the given command
     *
     * @param command Command to execute
     * @return [Result] of [BufferedSource] for parsing
     */
    suspend fun <T> execute(command: Command<T>): Result<T> = withContext(context) {
        val processBuilder = ProcessBuilder(ADB, command.command).apply {
            redirectErrorStream(true)
        }
        val process = try {
            processBuilder.start()
        } catch (t: Throwable) {
            return@withContext Result.failure(t)
        }
        process
            .inputStream
            .source()
            .buffer()
            .use { source ->
                try {
                    Result.success(command.execute(source))
                } catch (t: Throwable) {
                    Result.failure(t)
                }
            }
    }

    companion object {
        private const val ADB = "adb"

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