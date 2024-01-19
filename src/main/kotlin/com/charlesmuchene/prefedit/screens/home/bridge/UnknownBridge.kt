package com.charlesmuchene.prefedit.screens.home.bridge

import androidx.compose.foundation.layout.*
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.ui.padding

@Composable
fun UnknownBridge(modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator()
        Spacer(modifier = Modifier.width(padding))
        Text(text = bundle[HomeKey.UnknownBridgeStatus], style = typography.h5)
    }
}