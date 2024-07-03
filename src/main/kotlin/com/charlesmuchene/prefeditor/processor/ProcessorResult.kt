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

/**
 * Processor result
 *
 * For our use cases, 0 and 1 are treated as success.
 * For example: Invoking executable without any args exits with code 1
 *
 * @param exitCode Exit code of running a system process
 * @param output [ByteArray] to support processing binary files
 */
data class ProcessorResult(val exitCode: Int, val output: ByteArray) {
    val isSuccess: Boolean = exitCode == 0 || exitCode == 1

    val outputString get() = String(bytes = output)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProcessorResult

        if (exitCode != other.exitCode) return false
        if (!output.contentEquals(other.output)) return false
        if (isSuccess != other.isSuccess) return false

        return true
    }

    override fun hashCode(): Int {
        var result = exitCode
        result = 31 * result + output.contentHashCode()
        result = 31 * result + isSuccess.hashCode()
        return result
    }

    companion object {
        fun failure(
            exitCode: Int = 105,
            output: String = "Processor failure. See log for details.",
        ) = ProcessorResult(exitCode = exitCode, output = output.toByteArray())
    }
}
