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

package com.charlesmuchene.prefedit.command

import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Pref
import com.charlesmuchene.prefedit.data.PrefFile
import com.charlesmuchene.prefedit.parser.Parser
import com.charlesmuchene.prefedit.parser.PrefParser

data class FetchPref(
    private val app: App,
    private val device: Device,
    private val prefFile: PrefFile,
    override val parser: Parser<Pref> = PrefParser(),
) : Command<Pref> {
    override val command: String = "command-moto-moto"
}
