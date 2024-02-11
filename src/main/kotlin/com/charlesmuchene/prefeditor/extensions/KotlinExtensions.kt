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

package com.charlesmuchene.prefeditor.extensions

import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.onEach

fun <T> Result<T>.eval(logger: KLogger): Result<T> {
    onFailure { throwable -> logger.error(throwable) { "kotlin.Result.eval" } }
    return this
}

fun <T> Flow<T>.throttleLatest(delayMillis: Long): Flow<T> = conflate().onEach { delay(delayMillis) }

fun <T> Flow<T>.useCaseTransform(): Flow<T> = drop(count = 1).throttleLatest(delayMillis = 150)

val editorLogger = KotlinLogging.logger(name = "General logger")