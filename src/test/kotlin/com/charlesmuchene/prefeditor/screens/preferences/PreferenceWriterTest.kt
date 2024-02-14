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
import com.charlesmuchene.prefeditor.data.Edit
import com.charlesmuchene.prefeditor.processor.Processor
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.StandardTestDispatcher
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
    private val dispatcher = StandardTestDispatcher(scheduler = scheduler)

    private lateinit var editor: PreferenceWriter

    private lateinit var processor: Processor

    @BeforeEach
    fun setup() {
        val command = DesktopWriteCommand(path = path.pathString)
        processor = mockk(relaxed = true) {
            coEvery {
                run(command = any(), config = eq({}))
            } returns Result.success("")
        }
        editor = PreferenceWriter(processor = processor, command = command)
    }


    @Test
    fun `add edit invokes processor with end root tag`() = runTest(scheduler) {
        val content = "content"
        val add = Edit.Add(content = content)

        editor.edit(edit = add)

        coVerify { processor.run(listOf("sh", "desktop.sh", "add", "<\\/map>", content, path.pathString), any()) }
    }
}