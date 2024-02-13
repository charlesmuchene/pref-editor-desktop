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

package com.charlesmuchene.prefeditor.processor

import com.charlesmuchene.prefeditor.extensions.editorLogger
import com.charlesmuchene.prefeditor.files.EditorFiles
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.path.pathString

/**
 * Run a system process
 *
 * @param context [CoroutineContext] for use when running actions
 */
class Processor(private val context: CoroutineContext = Dispatchers.IO) {

    suspend fun run(command: List<String>, config: ProcessBuilder.() -> Unit = {}): Result<String> =
        withContext(context) {
        editorLogger.debug { command }
            val builder = ProcessBuilder(command).apply {
                val scriptsPath = EditorFiles.scriptsPath().pathString
                environment()[PATH] += ":$scriptsPath"
                redirectErrorStream(true)
                config()
            }

            val process = try {
                builder.start()
            } catch (t: Throwable) {
                return@withContext Result.failure(t)
            }

            try {
                val output = async { BufferedReader(InputStreamReader(process.inputStream)).readText() }
                runInterruptible { process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS) }
                Result.success(output.await().trim())
            } catch (exception: CancellationException) {
                throw exception // propagate cancellation
            }
        }

    companion object {
        private const val PATH = "PATH"
        private const val TIMEOUT_SECONDS = 10L
    }
}