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

package com.charlesmuchene.prefeditor.command

import com.charlesmuchene.prefeditor.data.Edit
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class WriteCommandTest {
    private lateinit var command: WriteCommand

    @BeforeEach
    fun setup() {
        command = Command()
    }

    @Test
    fun `escape algorithm`() {
        with(command) {
            assertEquals(expected = "", actual = "".escaped())
            assertEquals(expected = "<map>", actual = "<map>".escaped())
            assertEquals(expected = "<\\/map>", actual = "</map>".escaped())
            assertEquals(
                expected = "<file device=\\\"1234\\\" app=\\\"package\\\">filename<\\/file>",
                actual = "<file device=\"1234\" app=\"package\">filename</file>".escaped(),
            )
        }
    }

    private class Command : WriteCommand {
        override fun MutableList<String>.delete(edit: Edit.Delete) {}

        override fun MutableList<String>.change(edit: Edit.Change) {}

        override fun MutableList<String>.add(edit: Edit.Add) {}
    }
}
