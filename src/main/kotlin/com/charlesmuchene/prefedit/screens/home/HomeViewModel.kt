package com.charlesmuchene.prefedit.screens.home

import com.charlesmuchene.prefedit.data.Device

class HomeViewModel {

    fun formatAttributes(device: Device): String = device.attributes.joinToString(transform = { attribute ->
        "${attribute.name}:${attribute.value}"
    })
}