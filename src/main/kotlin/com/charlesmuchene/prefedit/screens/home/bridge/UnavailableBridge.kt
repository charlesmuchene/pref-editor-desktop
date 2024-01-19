package com.charlesmuchene.prefedit.screens.home.bridge

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.ui.SingleText

@Composable
fun UnavailableBridge(modifier: Modifier = Modifier) {
    SingleText(key = HomeKey.UnavailableBridgeStatus, modifier = modifier)
}