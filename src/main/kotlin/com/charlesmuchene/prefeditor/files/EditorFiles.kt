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

import com.charlesmuchene.prefeditor.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.preferences.PreferencesCodec
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

/**
 * All operations in this class perform IO.
 */
object EditorFiles {

    private const val DIR = ".pref-editor"
    private const val PUSH_FILE = "push.sh"
    private const val ADD_FILE = "add.sh"
    private const val CHANGE_FILE = "change.sh"
    private const val DELETE_FILE = "delete.sh"
    private const val HOME_DIR = "user.home"
    private const val SCRIPTS_DIR = "scripts"
    private const val PREFERENCES = "preferences.xml"

    val appPath: Path = Paths.get(System.getProperty(HOME_DIR), DIR)

    fun preferencesPath(): Path = appPath.resolve(PREFERENCES)

    fun initialize(codec: PreferencesCodec) {
        ensurePathExists(path = appPath)
        createAppPreferences(path = preferencesPath(), codec = codec)
        copyScripts()
    }

    private fun ensurePathExists(path: Path) {
        if (path.exists()) return
        Files.createDirectory(path)
    }

    private fun createAppPreferences(path: Path, codec: PreferencesCodec) {
        if (path.exists()) return
        val content = codec.encodeDocument { tag("version") { text("1.0") } }
        Files.writeString(path, content.trim())
    }

    // TODO Move to scripts dir
    private fun scriptsPath(): Path = appPath.apply {
        ensurePathExists(this)
    }

    private fun copyScripts() {
        val path = scriptsPath()
        listOf(PUSH_FILE, ADD_FILE, DELETE_FILE, CHANGE_FILE).forEach { name ->
            copyScript(name = name, path = path.resolve(name))
        }
    }

    private fun copyScript(name: String, path: Path) {
        if (!path.exists()) javaClass.classLoader.getResourceAsStream("$SCRIPTS_DIR/$name")?.let {
            Files.copy(it, path)
        }
    }

}