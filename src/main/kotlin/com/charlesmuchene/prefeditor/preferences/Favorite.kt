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

package com.charlesmuchene.prefeditor.preferences

/**
 * A developer favorite
 *
 * ```
 * <?xml version='1.0' encoding='utf-8' standalone='yes' ?>
 * <map>
 *     ...
 *     <device>1B241CAA5079LR</device>
 *     <app device="1B241CAA5079LR">com.charlesmuchene.player</app>
 *     <file device="1B241CAA5079LR" app="com.charlesmuchene.player">preference.xml</file>
 * </map>
 * ```
 */
interface Favorite {
    data class Device(val serial: String) : Favorite
    data class App(val device: Device, val packageName: String) : Favorite
    data class File(val device: Device, val app: App, val name: String) : Favorite
}