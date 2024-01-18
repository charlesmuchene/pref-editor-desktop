package com.charlesmuchene.prefedit.resources

import java.util.*

object TextBundle {

    private val texts: Properties? by lazy(::loadText)

    operator fun get(key: TextKey): String = texts?.getProperty(key.key) ?: DEFAULT_MESSAGE

    private fun loadText(): Properties? {
        val resource = javaClass.classLoader.getResource(BUNDLE_NAME) ?: return null
        return Properties().apply {
            resource.openStream().use(::load)
        }
    }

    private const val DEFAULT_MESSAGE = "--"
    private const val BUNDLE_NAME = "TextBundle.properties"
}

enum class TextKey(val key: String) {
    Title(key = "app.title")
}