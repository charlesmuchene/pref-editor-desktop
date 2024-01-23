package com.charlesmuchene.prefedit.screens.preferences.editor

import com.charlesmuchene.prefedit.data.*
import org.junit.jupiter.api.Test

class EditorViewModelTest {

    private val prefs = Preferences(
        entries = listOf(
            BooleanEntry(name = "boolean", value = false),
            StringEntry(name = "string", value = "string"),
            IntEntry(name = "another", value = 0),
            IntEntry(name = "integer", value = -1),
            FloatEntry(name = "float", value = 0.0f),
            SetEntry(name = "string-set", entries = listOf("strings", "one", "two", "three")),
            LongEntry(name = "long", value = 0)
        )
    )

    @Test
    fun `test view model`() {
        println(prefs)
    }
}