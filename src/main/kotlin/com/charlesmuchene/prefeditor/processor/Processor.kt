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

import com.charlesmuchene.prefeditor.files.PrefEditorFiles
import kotlinx.coroutines.*
import okio.Buffer
import okio.buffer
import okio.source
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

    suspend fun run(command: List<String>, config: ProcessBuilder.() -> Unit = {}) = withContext(context) {
        val builder = ProcessBuilder(command).apply {
            environment()[PATH] += ":${PrefEditorFiles.appPath().pathString}"
            redirectErrorStream(true)
            config()
        }
        val buffer = Buffer()
        val process = builder.start()
        val result = async {
            process
                .inputStream
                .source()
                .buffer().use { source ->
                    while (true) {
                        yield()
                        val line = source.readUtf8Line() ?: break
                        buffer.writeUtf8(line)
                        buffer.writeUtf8(System.lineSeparator())
                    }
                    buffer.readUtf8()
                }
        }
        try {
            runInterruptible { process.waitFor(10, TimeUnit.SECONDS) }
            result.await()
        } catch (exception: CancellationException) {
            throw exception // propagate cancellation
        }
    }

    companion object {
        private const val PATH = "PATH"
    }
}