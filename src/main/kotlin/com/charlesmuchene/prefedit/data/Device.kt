package com.charlesmuchene.prefedit.data


typealias Devices = List<Device>
typealias Attributes = List<Device.Attribute>

data class Device(val serial: String, val type: Type, val attributes: Attributes) {
    enum class Type {
        Device, Unauthorized, Unknown
    }

    data class Attribute(val name: String, val value: String)
}
