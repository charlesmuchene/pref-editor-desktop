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

import com.charlesmuchene.prefeditor.screens.preferences.PreferenceEncoder.Encoder.attrib
import com.charlesmuchene.prefeditor.screens.preferences.PreferenceEncoder.Encoder.tag
import com.charlesmuchene.prefeditor.screens.preferences.PreferencesCodec
import com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.theme.ThemeCodec
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import kotlin.io.path.exists

/**
 * All operations in this class perform IO.
 */
object EditorFiles {

    private const val DIR = ".pref-editor"
    private const val HOME_DIR = "user.home"
    private const val SCRIPTS_DIR = "scripts"
    private const val DEVICE_FILE = "device.sh"
    private const val DESKTOP_FILE = "desktop.sh"
    private const val PREFERENCES = "preferences.xml"

    private val context = Dispatchers.IO

    private val appPath: Path = Paths.get(System.getProperty(HOME_DIR), DIR)

    fun preferencesPath(): Path = appPath.resolve(PREFERENCES)

    suspend fun scriptsPath(): Path = appPath.resolve(SCRIPTS_DIR).apply {
        ensurePathExists(path = this)
    }

    suspend fun initialize(codec: PreferencesCodec) {
        ensurePathExists(path = appPath)
        createAppPreferences(path = preferencesPath(), codec = codec)
        copyScripts()
    }

    private suspend fun createAppPreferences(path: Path, codec: PreferencesCodec) = withContext(context) {
        if (!path.exists()) {
            val content = codec.encodeDocument {
                tag("version") { attrib(name = "value", value = "1.0") }
                tag(ThemeCodec.THEME) { attrib(name = "value", value = "2") }
            }
            Files.writeString(path, content.trim())
        }
    }

    private suspend fun copyScripts() {
        val path = scriptsPath()
        listOf(DESKTOP_FILE, DEVICE_FILE).forEach { name ->
            copyScript(name = name, path = path.resolve(name))
        }
    }

    private suspend fun copyScript(name: String, path: Path) = withContext(context) {
        if (!path.exists()) {
            val resourceName = "$SCRIPTS_DIR/$name"
            javaClass.classLoader.getResourceAsStream(resourceName)?.let {
                Files.copy(it, path)
            }
        }
    }

    private suspend fun ensurePathExists(path: Path) = withContext(context){
        if (!path.exists()) Files.createDirectory(path)
    }

}