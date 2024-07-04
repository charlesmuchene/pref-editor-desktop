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

package com.charlesmuchene.prefeditor

import com.charlesmuchene.prefeditor.data.App
import com.charlesmuchene.prefeditor.data.BooleanPreference
import com.charlesmuchene.prefeditor.data.Device
import com.charlesmuchene.prefeditor.data.FloatPreference
import com.charlesmuchene.prefeditor.data.IntPreference
import com.charlesmuchene.prefeditor.data.LongPreference
import com.charlesmuchene.prefeditor.data.PrefFile
import com.charlesmuchene.prefeditor.data.KeyValuePreferences
import com.charlesmuchene.prefeditor.data.SetPreference
import com.charlesmuchene.prefeditor.data.StringPreference
import com.charlesmuchene.prefeditor.screens.apps.AppListCommand
import java.nio.file.Files
import java.nio.file.Path

object TestFixtures {
    const val EXECUTABLE = "exec"

    val executablePath: Path = Path.of(EXECUTABLE)

    val device =
        Device(
            serial = "1B241CAA5079LR",
            type = Device.Type.Device,
            attributes =
                listOf(
                    Device.Attribute(name = "usb", value = "1O845693Y"),
                    Device.Attribute(name = "product", value = "redfin"),
                    Device.Attribute(name = "model", value = "Pixel_5"),
                    Device.Attribute(name = "device", value = "redfin"),
                    Device.Attribute(name = "transport_id", value = "4"),
                ),
        )

    val deviceOne =
        Device(
            serial = "1B241CAA5079LR",
            type = Device.Type.Device,
            attributes =
                listOf(
                    Device.Attribute(name = "usb", value = "1O845693Y"),
                    Device.Attribute(name = "product", value = "redfin"),
                    Device.Attribute(name = "model", value = "Pixel_5"),
                    Device.Attribute(name = "device", value = "redfin"),
                    Device.Attribute(name = "transport_id", value = "4"),
                ),
        )

    val deviceTwo =
        Device(
            serial = "emulator-5554",
            type = Device.Type.Device,
            attributes =
                listOf(
                    Device.Attribute(name = "product", value = "sdk_gphone64_arm64"),
                    Device.Attribute(name = "model", value = "sdk_gphone64_arm64"),
                    Device.Attribute(name = "device", value = "emu64a"),
                    Device.Attribute(name = "transport_id", value = "1"),
                ),
        )

    val unauthorized =
        Device(
            serial = "1B241CAA5079LR",
            type = Device.Type.Unauthorized,
            attributes =
                listOf(
                    Device.Attribute(name = "usb", value = "1O845693Y"),
                    Device.Attribute(name = "transport_id", value = "4"),
                ),
        )

    val app = App(packageName = "com.charlesmuchene.pref-editor")

    val keyValuePrefFile = PrefFile(name = "preferences.xml", type = PrefFile.Type.KEY_VALUE)
    val datastorePrefFile = PrefFile(name = "preferences.preferences_pb", type = PrefFile.Type.DATA_STORE)

    val prefs =
        KeyValuePreferences(
            preferences =
                listOf(
                    BooleanPreference(name = "boolean", value = "false"),
                    StringPreference(name = "string", value = "string"),
                    IntPreference(name = "another-integer", value = "0"),
                    StringPreference(name = "empty-string", value = ""),
                    IntPreference(name = "integer", value = "-1"),
                    FloatPreference(name = "float", value = "0.0"),
                    SetPreference(name = "string-set", entries = listOf("strings", "one", "two", "three")),
                    LongPreference(name = "long", value = "0"),
                ),
        )

    const val PREFERENCES = """<?xml version='1.0' encoding='utf-8' standalone='yes' ?>
                <map>
                    <boolean name="boolean" value="false" />
                    <string name="string">string</string>
                    <int name="integer" value="-1" />
                    <int name="another-integer" value="0" />
                    <float name="float" value="0.0" />
                    <string name="empty-string"></string>
                    <set name="string-set">
                        <string>strings</string>
                        <string>one</string>
                        <string>two</string>
                        <string>three</string>
                    </set>
                    <long name="long" value="100" />
                </map>
            """

    val APP_LIST_OUTPUT =
        """
        package:com.charlesmuchene.player
        package:com.charlesmuchene.now
        package:com.charlesmuchene.works
        package:com.charlesmuchene.in
        package:com.charlesmuchene.compose
        """.trimIndent()

    val appList =
        listOf(
            App(packageName = "com.charlesmuchene.player"),
            App(packageName = "com.charlesmuchene.now"),
            App(packageName = "com.charlesmuchene.works"),
            App(packageName = "com.charlesmuchene.in"),
            App(packageName = "com.charlesmuchene.compose"),
        )

    val appListCommand = AppListCommand(device = device, executable = EXECUTABLE)

    fun emptyPreferences(): String {
        val url = javaClass.classLoader.getResource("empty-preferences.xml") ?: error("Missing empty preferences file")
        val path = Path.of(url.toURI())
        return Files.readAllLines(path).joinToString(separator = "\r\n")
    }
}
