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

package com.charlesmuchene.prefeditor.resources

import java.util.Properties

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

enum class AppKey(override val key: String) : TextKey {
    Title(key = "app.title"),
}

enum class DevicesKey(override val key: String) : TextKey {
    BridgeUnavailable(key = "devices.bridge.unavailable"),
    BridgeUnavailableSecondary(key = "devices.bridge.unavailable.secondary"),
    EmptyDeviceList(key = "devices.listing.empty"),
    DeviceListLoading(key = "devices.listing.loading"),
    DeviceListError(key = "devices.listing.error"),
    ConnectedDevices(key = "devices.connected.devices"),
    UnauthorizedDevice(key = "devices.unauthorized.device"),
    UnknownDevice(key = "devices.unknown.device"),
}

enum class AppsKey(override val key: String) : TextKey {
    AppListingTitle(key = "apps.listing.title"),
    AppListingError(key = "apps.listing.error"),
    AppListingLoading(key = "apps.listing.loading"),
}

enum class PrefFilesKey(override val key: String) : TextKey {
    PrefListingError(key = "pref.files.listing.error"),
    PrefListingLoading(key = "pref.files.listing.loading"),
    PrefListingTitle(key = "pref.files.listing.title"),
    PrefListingEmpty(key = "pref.files.listing.empty"),
}

enum class PrefsKey(override val key: String) : TextKey {
    PrefLoading(key = "prefs.editor.loading"),
    PrefError(key = "prefs.editor.error"),
    PrefTitle(key = "prefs.editor.title"),
}
