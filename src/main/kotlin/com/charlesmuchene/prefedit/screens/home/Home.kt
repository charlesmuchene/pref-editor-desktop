package com.charlesmuchene.prefedit.screens.home

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.charlesmuchene.prefedit.bridge.Bridge
import com.charlesmuchene.prefedit.bridge.BridgeStatus
import com.charlesmuchene.prefedit.bridge.BridgeStatus.*
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.AppKey
import com.charlesmuchene.prefedit.screens.home.bridge.AvailableBridge
import com.charlesmuchene.prefedit.screens.home.bridge.UnavailableBridge
import com.charlesmuchene.prefedit.screens.home.bridge.UnknownBridge
import kotlinx.coroutines.Dispatchers

@Preview
@Composable
fun Home(bridge: Bridge, modifier: Modifier = Modifier) {
    var bridgeStatus by remember(bridge) { mutableStateOf<BridgeStatus>(Unknown) }

    LaunchedEffect(Unit) {
        bridgeStatus = Bridge.checkBridge(context = coroutineContext + Dispatchers.IO)
    }

    MaterialTheme {
        Scaffold(
            topBar = { MainBar() }
        ) {
            when (bridgeStatus) {
                Unavailable -> UnavailableBridge(modifier = modifier.padding(it))
                Available -> AvailableBridge(modifier = modifier.padding(it))
                Unknown -> UnknownBridge(modifier = modifier.padding(it))
            }
        }
    }
}

@Composable
fun MainBar(modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current

    TopAppBar(modifier = modifier, title = {
        Text(
            maxLines = 1,
            text = bundle[AppKey.HomeTitle],
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
            style = MaterialTheme.typography.h6
        )
    })
}