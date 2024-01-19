package com.charlesmuchene.prefedit.bridge

sealed interface BridgeStatus {
    data object Unknown : BridgeStatus
    data object Available : BridgeStatus
    data object Unavailable : BridgeStatus
}