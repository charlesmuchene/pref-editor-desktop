package com.charlesmuchene.prefeditor.processor

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertTrue

class ProcessorTest {
    private lateinit var processor: Processor
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        processor = Processor(dispatcher)
    }

    @Test
    fun `process has the scripts dir in its path`() =
        runTest(dispatcher) {
            processor.run(listOf("time")) {
                assertTrue(environment()["PATH"]!!.contains("scripts"))
            }
        }
}
