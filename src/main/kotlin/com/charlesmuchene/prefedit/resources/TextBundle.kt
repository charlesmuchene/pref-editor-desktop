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

package com.charlesmuchene.prefedit.resources

import java.util.*

class TextBundle {

    private val texts: Properties? by lazy(::loadText)

    operator fun get(key: TextKey): String = texts?.getProperty(key.key) ?: DEFAULT_MESSAGE

    private fun loadText(): Properties? {
        val resource = javaClass.classLoader.getResource(BUNDLE_NAME) ?: return null
        return Properties().apply {
            resource.openStream().use(::load)
        }
    }

    companion object {
        private const val DEFAULT_MESSAGE = "--"
        private const val BUNDLE_NAME = "TextBundle.properties"
    }
}

interface TextKey {
    val key: String
}

enum class ApplicationKey(override val key: String) : TextKey {
    Title(key = "app.title"),
    HomeTitle(key = "app.main.window.title"),
}

enum class HomeKey(override val key: String) : TextKey {
    UnknownBridgeStatus(key = "home.unknown.bridge.status"),
    UnavailableBridgeStatus(key = "home.unavailable.bridge.status"),
    EmptyDeviceList(key = "home.empty.device.list"),
    DeviceListError(key = "home.device.list.error"),
    ConnectedDevices(key = "home.connected.devices"),
    UnauthorizedDevice(key = "home.device.unauthorized"),
    UnknownDevice(key = "home.device.unknown"),
}

enum class DeviceKey(override val key: String) : TextKey {
    AppListingTitle(key = "device.app.listing.title"),
    AppListingError(key = "device.app.listing.error"),
    AppListingLoading(key = "device.app.listing.loading"),
}

enum class AppKey(override val key: String) : TextKey {
    PrefListingError(key = "app.pref.listing.error"),
    PrefListingLoading(key = "app.pref.listing.loading"),
    PrefListingTitle(key = "app.pref.listing.title"),
    PrefListingEmpty(key = "app.pref.listing.empty"),
}