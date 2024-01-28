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

package com.charlesmuchene.prefedit

import com.charlesmuchene.prefedit.data.*

object TestFixtures {

    val device = Device(
        serial = "1B241CAA5079LR", type = Device.Type.Device, attributes = listOf(
            Device.Attribute(name = "usb", value = "1O845693Y"),
            Device.Attribute(name = "product", value = "redfin"),
            Device.Attribute(name = "model", value = "Pixel_5"),
            Device.Attribute(name = "device", value = "redfin"),
            Device.Attribute(name = "transport_id", value = "4"),
        )
    )

    val app = App(packageName = "com.charlesmuchene.pref-edit")

    val prefFile = PrefFile(name = "preferences.xml", type = PrefFile.Type.KEY_VALUE)

    val prefs = Preferences(
        entries = listOf(
            BooleanEntry(name = "boolean", value = "false"),
            StringEntry(name = "string", value = "string"),
            IntEntry(name = "another-integer", value = "0"),
            IntEntry(name = "integer", value = "-1"),
            FloatEntry(name = "float", value = "0.0"),
            SetEntry(name = "string-set", entries = listOf("strings", "one", "two", "three")),
            LongEntry(name = "long", value = "0")
        )
    )

}