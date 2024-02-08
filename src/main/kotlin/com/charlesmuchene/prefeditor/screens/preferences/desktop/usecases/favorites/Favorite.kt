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

package com.charlesmuchene.prefeditor.screens.preferences.desktop.usecases.favorites

/**
 * A developer's favorite
 *
 * Example representation in the preference file:
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
sealed interface Favorite {

    /**
     * A favorite device
     *
     * @param serial Ids the device
     */
    data class Device(val serial: String) : Favorite

    /**
     * A favorite app
     *
     * @param device Favorite [Device]'s serial
     * @param packageName Ids the app on the [device]
     */
    data class App(val device: String, val packageName: String) : Favorite

    /**
     * A favorite preference file
     *
     * @param device Favorite [Device]'s serial
     * @param app Favorite [App]'s package name
     * @param name Ids the file for the [app] in the [device]
     */
    data class File(val device: String, val app: String, val name: String) : Favorite
}