package com.charlesmuchene.prefedit.screens.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.bridge.BridgeStatus
import com.charlesmuchene.prefedit.bridge.BridgeStatus.*
import com.charlesmuchene.prefedit.screens.home.bridge.AvailableBridge
import com.charlesmuchene.prefedit.screens.home.bridge.UnavailableBridge
import com.charlesmuchene.prefedit.screens.home.bridge.UnknownBridge
import com.charlesmuchene.prefedit.ui.padding

@Preview
@Composable
fun Home(bridge: Bridge, modifier: Modifier = Modifier) {
    var bridgeStatus by remember(bridge) { mutableStateOf<BridgeStatus>(Available) }

    LaunchedEffect(Unit) {
//        bridgeStatus = Bridge.checkBridge(context = coroutineContext + Dispatchers.IO)
    }

    MaterialTheme {
        Content(bridgeStatus = bridgeStatus, modifier = modifier)
    }
}

@Composable
private fun Content(bridgeStatus: BridgeStatus, modifier: Modifier = Modifier) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = modifier.fillMaxSize()) {
        Text(
            text = "Shared Preferences Editor",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h3
        )
        Spacer(modifier = Modifier.height(padding))
        Divider(modifier = Modifier.fillMaxWidth().padding(horizontal = padding))
        when (bridgeStatus) {
            Unavailable -> UnavailableBridge()
            Available -> AvailableBridge()
            Unknown -> UnknownBridge()
        }
    }
}