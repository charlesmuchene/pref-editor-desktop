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

package com.charlesmuchene.prefeditor.screens.apps

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.Apps
import kotlinx.coroutines.yield

class AppListDecoder(private val sorted: Boolean = true) {
    suspend fun decode(content: String): Apps {
        val apps = mutableListOf<App>()
        content.lineSequence().forEach { line ->
            yield()
            apps.add(decodeApp(line = line))
        }
        if (sorted) apps.sortBy(App::packageName)
        return apps
    }

    private fun decodeApp(line: String): App {
        val packageName = line.split(DELIMITER)[1]
        return App(packageName = packageName)
    }

    private companion object {
        const val DELIMITER = ":"
    }
}
