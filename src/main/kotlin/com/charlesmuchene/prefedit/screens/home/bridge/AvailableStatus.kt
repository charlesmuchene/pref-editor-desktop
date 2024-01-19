package com.charlesmuchene.prefedit.screens.home.bridge

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.charlesmuchene.prefedit.command.ListDevices
import com.charlesmuchene.prefedit.data.Device
import com.charlesmuchene.prefedit.data.Devices
import com.charlesmuchene.prefedit.providers.LocalBridge
import com.charlesmuchene.prefedit.providers.LocalBundle
import com.charlesmuchene.prefedit.resources.HomeKey
import com.charlesmuchene.prefedit.ui.SingleText
import com.charlesmuchene.prefedit.ui.padding

@Composable
fun AvailableBridge(modifier: Modifier = Modifier) {

    val bridge = LocalBridge.current
    var result by remember(bridge) {
        mutableStateOf<Result<Devices>>(Result.success(emptyList()))
    }

    LaunchedEffect(Unit) {
        result = bridge.execute(command = ListDevices())
    }

    when {
        result.isSuccess -> result.getOrNull()?.let { devices ->
            if (devices.isEmpty()) NoDevices(modifier = modifier)
            else DeviceList(devices = devices, modifier = modifier)
        } ?: DeviceListError(modifier = modifier)

        result.isFailure -> DeviceListError(modifier = modifier)
    }
}

@Composable
@OptIn(ExperimentalFoundationApi::class)
private fun DeviceList(devices: Devices, modifier: Modifier = Modifier) {
    val bundle = LocalBundle.current
    val header = bundle[HomeKey.ConnectedDevices]
    LazyColumn(modifier = modifier.fillMaxSize().padding(vertical = padding)) {
        stickyHeader {
            Text(text = header, style = typography.h5)
            Spacer(modifier = Modifier.height(padding))
        }
        items(items = devices, key = Device::serial) { device ->
            DeviceRow(device = device)
        }
    }
}

@Composable
private fun DeviceRow(device: Device, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = device.name, style = typography.h6)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = device.serial, style = typography.subtitle1)
    }
}

@Composable
private fun DeviceListError(modifier: Modifier = Modifier) {
    SingleText(key = HomeKey.DeviceListError, modifier = modifier)
}

@Composable
private fun NoDevices(modifier: Modifier = Modifier) {
    SingleText(key = HomeKey.EmptyDeviceList, modifier = modifier)
}