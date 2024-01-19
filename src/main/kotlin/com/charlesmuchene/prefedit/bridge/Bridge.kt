package com.charlesmuchene.prefedit.bridge

import com.charlesmuchene.prefedit.bridge.BridgeStatus.Available
import com.charlesmuchene.prefedit.bridge.BridgeStatus.Unavailable
import com.charlesmuchene.prefedit.command.Command
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import okio.BufferedSource
import okio.buffer
import okio.source
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

            val async = async { builder.start().waitFor() == 0 }

            try {
                if (async.await()) Available else Unavailable
            } catch (e: Exception) {
                Unavailable
            }
        }
    }
}