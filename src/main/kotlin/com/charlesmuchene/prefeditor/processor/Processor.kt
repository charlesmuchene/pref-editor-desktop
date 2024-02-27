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

import com.charlesmuchene.prefeditor.exceptions.ProcessorTimeoutException
import com.charlesmuchene.prefeditor.files.EditorFiles
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runInterruptible
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException
import kotlin.io.path.pathString

private val logger = KotlinLogging.logger { }

/**
 * Run a system process
 *
 * @param context [CoroutineContext] for use when running actions
 */
class Processor(private val context: CoroutineContext = Dispatchers.IO) {
    suspend fun run(
        command: List<String>,
        config: ProcessBuilder.() -> Unit = {},
    ): ProcessorResult =
        withContext(context) {
            val builder =
                ProcessBuilder(command).apply {
                    val scriptsPath = EditorFiles.scriptsPath().pathString
                    environment()[PATH] += ":$scriptsPath"
                    redirectErrorStream(true)
                    config()
                }

            val process =
                try {
                    logger.debug { "Executing: $command" }
                    builder.start()
                } catch (exception: IOException) {
                    logger.error(exception) { "Starting the process" }
                    return@withContext ProcessorResult.failure()
                } catch (exception: SecurityException) {
                    logger.error(exception) { "Starting the process" }
                    return@withContext ProcessorResult.failure()
                } catch (exception: UnsupportedOperationException) {
                    logger.error(exception) { "Starting the process" }
                    return@withContext ProcessorResult.failure()
                }

            try {
                val deferredText =
                    async {
                        BufferedReader(InputStreamReader(process.inputStream))
                            .use(BufferedReader::readText)
                    }
                if (runInterruptible { process.waitFor(TIMEOUT_SECONDS, TimeUnit.SECONDS) }) {
                    ProcessorResult(exitCode = process.exitValue(), output = deferredText.await().trim())
                } else {
                    logger.error(ProcessorTimeoutException("Timed out waiting $TIMEOUT_SECONDS for $command")) {}
                    ProcessorResult.failure()
                }
            } catch (exception: CancellationException) {
                println(exception.message)
                throw exception // propagate cancellation
            }
        }

    companion object {
        private const val PATH = "PATH"
        private const val TIMEOUT_SECONDS = 10L
    }
}
