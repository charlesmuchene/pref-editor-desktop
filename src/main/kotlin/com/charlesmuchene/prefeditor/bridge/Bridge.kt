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
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException
import kotlin.coroutines.CoroutineContext

/**
 * Interface to the debug bridge
 *
 * Current implementation expects the bridge to be on the system `PATH`
 *
 * @param context [CoroutineContext] for all bridge operations
 */
class Bridge(
    private val processor: Processor = Processor(),
    private val context: CoroutineContext = Dispatchers.IO + CoroutineName(name = "Bridge"),
) {
    suspend fun checkBridge(): BridgeStatus = withContext(context) {
        try {
            processor.run(listOf("adb")) {
                redirectOutput(ProcessBuilder.Redirect.DISCARD)
                redirectError(ProcessBuilder.Redirect.DISCARD)
            }
            Available
        } catch (_: IOException) {
            Unavailable
        }
    }

    companion object {
        private const val DELIMITER = " "
        private const val CONTENT = "PREF_EDITOR_CONTENT"
    }
}