package com.sasy.nontag.utils.bluetooth_utils

import android.bluetooth.BluetoothDevice

interface BluetoothHelperListener {

    fun onStartDiscovery()

    fun onFinishDiscovery()

    fun onEnabledBluetooth()

    fun onDisabledBluetooh()

    /**
     * You can detect nearby devices with this listener.
     */
    interface onDetectNearbyDeviceListener {
        fun onDeviceDetected(device: BluetoothDevice?)
    }


    /**
     * You can get bluetooth discovery started or finished with this listener.
     */
    interface onDiscoveryStateChangedListener {
        fun onDiscoveryStateChanged(state: Int)
    }
}