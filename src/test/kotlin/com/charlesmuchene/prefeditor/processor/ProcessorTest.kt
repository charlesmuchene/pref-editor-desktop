package com.charlesmuchene.prefeditor.processor

import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import kotlin.test.Ignore
import kotlin.test.assertTrue

fun successProcessorResult(output: String = "") = ProcessorResult(exitCode = 0, output = output.toByteArray())

class ProcessorTest {
    private lateinit var processor: Processor
    private val dispatcher = StandardTestDispatcher()

    @BeforeEach
    fun setup() {
        processor = Processor(dispatcher)
    }

    @Ignore // TODO Add scripts dir in CI
    fun `process has the scripts dir in its path`() =
        runTest(dispatcher) {
            processor.run(listOf("time")) {
                assertTrue(environment()["PATH"]!!.contains("scripts"))
            }
        }
}
