package com.charlesmuchene.prefeditor.app

import com.charlesmuchene.prefeditor.models.AppStatus
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.processor.ProcessorResult
import com.charlesmuchene.prefeditor.processor.successProcessorResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.test.Test
import kotlin.test.assertTrue

class AppSetupTest {
    @TempDir
    private lateinit var path: Path

    @Test
    fun `app setup with adb available ready status`() =
        runTest {
            val processor =
                mockk<Processor> {
                    coEvery { run(any(), any()) } returns successProcessorResult()
                }

            val status = appSetup(pathOverride = path, processor = processor)

            assertTrue(status is AppStatus.Ready)
        }

    @Test
    fun `app setup with missing adb returns no bridge status`() =
        runTest {
            val processor =
                mockk<Processor> {
                    coEvery { run(any(), any()) } returns ProcessorResult.failure()
                }

            val status = appSetup(pathOverride = path, processor = processor)

            assertTrue(status is AppStatus.NoBridge)
        }
}
