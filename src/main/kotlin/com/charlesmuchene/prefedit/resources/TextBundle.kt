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

enum class AppKey(override val key: String) : TextKey {
    Title(key = "app.title")
}

enum class HomeKey(override val key: String) : TextKey {
    UnknownBridgeStatus(key = "home.unknown.bridge.status"),
    UnavailableBridgeStatus(key = "home.unavailable.bridge.status"),
    EmptyDeviceList(key = "home.empty.device.list"),
    DeviceListError(key = "home.device.list.error"),
    ConnectedDevices(key = "home.connected.devices"),
}