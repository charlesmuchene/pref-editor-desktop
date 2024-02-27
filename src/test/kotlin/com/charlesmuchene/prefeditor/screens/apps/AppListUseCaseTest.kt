package com.charlesmuchene.prefeditor.screens.apps

import app.cash.turbine.test
import com.charlesmuchene.prefeditor.TestFixtures.APP_LIST_OUTPUT
import com.charlesmuchene.prefeditor.TestFixtures.appList
import com.charlesmuchene.prefeditor.TestFixtures.appListCommand
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.processor.successProcessorResult
import com.charlesmuchene.prefeditor.screens.apps.AppListUseCase.FetchStatus.Done
import com.charlesmuchene.prefeditor.screens.apps.AppListUseCase.FetchStatus.Fetching
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class AppListUseCaseTest {
    private val processor =
        mockk<Processor> {
            coEvery { run(any(), any()) } returns successProcessorResult(APP_LIST_OUTPUT)
        }
    private val decoder =
        mockk<AppListDecoder> {
            coEvery { decode(APP_LIST_OUTPUT) } returns appList
        }

    @Test
    fun `use case resets list and sends fetched content`() =
        runTest {
            val useCase = AppListUseCase(command = appListCommand, processor = processor, decoder = decoder)
            useCase.status.test {
                useCase.fetch()
                assertTrue(awaitItem() is Fetching)
                val item = awaitItem()
                assertTrue(item is Done)
                assertEquals(expected = appList, actual = item.apps)
            }
        }
}
