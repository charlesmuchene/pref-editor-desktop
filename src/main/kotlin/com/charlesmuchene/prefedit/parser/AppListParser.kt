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

package com.charlesmuchene.prefedit.parser

import com.charlesmuchene.prefedit.data.App
import com.charlesmuchene.prefedit.data.Apps
import okio.BufferedSource

class AppListParser(private val sorted: Boolean = true) : Parser<Apps> {

    override fun parse(source: BufferedSource): Apps {
        val listing = buildList {
            while (true) {
                val line = source.readUtf8Line() ?: break
                add(parseApp(line = line))
            }
        }
        return if (sorted) listing.sortedBy(App::packageName) else listing
    }

    private fun parseApp(line: String): App {
        val packageName = line.split(":")[1]
        return App(packageName = packageName)
    }
}