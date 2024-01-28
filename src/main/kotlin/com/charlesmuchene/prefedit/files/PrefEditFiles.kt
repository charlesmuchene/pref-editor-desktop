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

package com.charlesmuchene.prefedit.files

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

object PrefEditFiles {

    private const val DIR = ".pref-edit"
    private const val PUSH_FILE = "push.sh"
    private const val HOME_DIR = "user.home"
    private const val SCRIPTS_DIR = "scripts"

    fun appPath(): Path = Paths.get(System.getProperty(HOME_DIR), DIR).apply {
        if (!exists()) Files.createDirectory(this)
    }

    fun copyPushScript() {
        val inputStream = javaClass.classLoader.getResourceAsStream("$SCRIPTS_DIR/$PUSH_FILE") ?: return
        val path = appPath().resolve(PUSH_FILE)
        /*if (!path.exists()) */Files.copy(inputStream, path)
    }

}