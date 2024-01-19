package com.charlesmuchene.prefedit.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.TextKey

@Composable
fun SingleText(key: TextKey, modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current
    val text = bundle[key]

    Box(modifier = modifier.fillMaxSize().padding(top = padding), contentAlignment = Alignment.TopCenter) {
        Text(text = text, style = MaterialTheme.typography.h5)
    }
}