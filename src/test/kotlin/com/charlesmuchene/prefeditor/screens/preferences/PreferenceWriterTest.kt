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

package com.charlesmuchene.prefeditor.screens.preferences

import com.charlesmuchene.prefeditor.command.DesktopWriteCommand
import com.charlesmuchene.prefeditor.command.WriteCommand
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.processor.Processor
import com.charlesmuchene.prefeditor.processor.successProcessorResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.io.TempDir
import java.nio.file.Path
import kotlin.io.path.pathString

class PreferenceWriterTest {
    @TempDir
    private lateinit var path: Path

    private val scheduler = TestCoroutineScheduler()

    private lateinit var writer: PreferenceWriter
    private lateinit var processor: Processor

    @BeforeEach
    fun setup() {
        val command = DesktopWriteCommand(path = path.pathString)
        processor =
            mockk(relaxed = true) {
                coEvery {
                    run(command = any(), config = eq({}))
                } returns successProcessorResult()
            }
        writer = PreferenceWriter(processor = processor, command = command)
    }

    @Test
    fun `add edit invokes processor with end root tag`() =
        runTest(scheduler) {
            val content = "content"
            val add = Edit.Add(content = content)

            writer.edit(edit = add)

            coVerify { processor.run(listOf("sh", "desktop.sh", "add", "<\\/map>", content, path.pathString), any()) }
        }

    @Test
    fun `edit invokes processor with respective command`() =
        runTest(scheduler) {
            val content = "content"
            val matcher = "matcher"
            val addCmd = listOf("add")
            val deleteCmd = listOf("delete")
            val changeCmd = listOf("change")
            val add = Edit.Add(content = content)
            val delete = Edit.Delete(matcher = matcher)
            val change = Edit.Change(matcher = matcher, content = content)

            val command =
                mockk<WriteCommand> {
                    every { command(add) } returns addCmd
                    every { command(delete) } returns deleteCmd
                    every { command(change) } returns changeCmd
                }
            val processor =
                mockk<Processor> {
                    coEvery { run(addCmd) } returns successProcessorResult(addCmd.first())
                    coEvery { run(deleteCmd) } returns successProcessorResult(deleteCmd.first())
                    coEvery { run(changeCmd) } returns successProcessorResult(changeCmd.first())
                }
            val writer = PreferenceWriter(processor, command)

            writer.edit(listOf(add, delete, change))
            coVerify { processor.run(addCmd) }
            coVerify { processor.run(deleteCmd) }
            coVerify { processor.run(changeCmd) }
        }
}
