package com.charlesmuchene.prefeditor.bridge

import com.charlesmuchene.prefeditor.TestFixtures.EXECUTABLE
import com.charlesmuchene.prefeditor.TestFixtures.executablePath
import com.charlesmuchene.prefeditor.processor.Processor
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class BridgeTest {
    private lateinit var bridge: Bridge

    private val processor = mockk<Processor>()

    @BeforeEach
    fun setup() {
        bridge = Bridge(processor)
    }

    @Test
    fun `return bridge unavailable if processor cannot find it`() =
        runTest {
            coEvery { processor.run(any(), any()) } returns Result.failure(Throwable("no bridge"))

            val result = bridge.checkBridge(executablePath)

            assertEquals(BridgeStatus.Unavailable, result)
        }

    @Test
    fun `return bridge available if processor finds it`() =
        runTest {
            coEvery { processor.run(any(), any()) } returns Result.success(EXECUTABLE)

            val result = bridge.checkBridge(executablePath)

            assertEquals(BridgeStatus.Available(EXECUTABLE), result)
        }
}
