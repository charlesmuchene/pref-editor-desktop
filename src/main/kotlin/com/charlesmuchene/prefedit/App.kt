@file:JvmName(name = "App")

package com.charlesmuchene.prefedit

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.charlesmuchene.prefedit.resources.TextBundle
import com.charlesmuchene.prefedit.resources.TextKey.Title

@Composable
@Preview
fun App() {
    MaterialTheme {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Text(text = "Shared Preferences Editor", textAlign = TextAlign.Center)
        }
    }
}

fun main() = application {
    val state = rememberWindowState(position = WindowPosition.Aligned(alignment = Alignment.Center))
    Window(state = state, title = TextBundle[Title], onCloseRequest = ::exitApplication) {
        App()
    }
}
