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

package com.charlesmuchene.prefeditor.files

import com.charlesmuchene.prefeditor.preferences.PreferenceManager
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

object PrefEditorFiles {

    private const val DIR = ".pref-editor"
    private const val PUSH_FILE = "push.sh"
    private const val HOME_DIR = "user.home"
    private const val SCRIPTS_DIR = "scripts"
    private const val PREFERENCES = "preferences.xml"

    private fun writeEmptyPreferences(path: Path) {
        val content = PreferenceManager().writeEmpty()
        Files.writeString(path, content)
    }

    fun appPath(): Path = Paths.get(System.getProperty(HOME_DIR), DIR).apply {
        if (!exists()) Files.createDirectory(this)
    }

    fun copyPushScript() {
        val path = appPath().resolve(PUSH_FILE)
        if (!path.exists()) {
            javaClass.classLoader.getResourceAsStream("$SCRIPTS_DIR/$PUSH_FILE")?.let {
                Files.copy(it, path)
            }
        }
    }

    fun preferencePath(): Path {
        val path = appPath().resolve(PREFERENCES)
        if (!path.exists()) writeEmptyPreferences(path)
        return path
    }

}