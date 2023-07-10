package com.sasy.nontag.utils.bluetooth_utils

sealed class BluetoothState<out String> {
    object ConnectingState : BluetoothState<Nothing>()
    object ConnectedState : BluetoothState<Nothing>()
    class Error(val errorMessage: String) : BluetoothState<String>()
}