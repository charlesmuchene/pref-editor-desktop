package com.charlesmuchene.prefeditor.desktop

import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceWriter
import io.mockk.*
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.nio.file.Path
import kotlin.test.assertEquals

class PreferenceWriterTest {

    private val scheduler = TestCoroutineScheduler()
    private val dispatcher = StandardTestDispatcher(scheduler = scheduler)

    private lateinit var editor: PreferenceWriter

    private lateinit var processor: Processor

    @BeforeEach
    fun setup() {
        processor = mockk(relaxed = true) { coEvery { run(command = any(), config = eq({})) } returns "" }
        editor = PreferenceWriter(processor = processor, context = dispatcher)
    }

    @Test
    fun `escape algorithm`() {
        with(editor) {
            assertEquals(expected = "", actual = "".escaped())
            assertEquals(expected = "<map>", actual = "<map>".escaped())
            assertEquals(expected = "<\\/map>", actual = "</map>".escaped())
            assertEquals(
                expected = "<file device=\\\"1234\\\" app=\\\"package\\\">filename<\\/file>",
                actual = "<file device=\"1234\" app=\"package\">filename</file>".escaped()
            )
        }
    }

    @Test
    fun `add edit invokes processor with end root tag`() = runTest(scheduler) {
        val add = Edit.Add(content = "content")
        val path = Path.of("path")

        editor.edit(edit = add, path = path)

        coVerify { processor.run(listOf("sh", "add.sh", "<\\/map>", "$path"), any())}
    }
}