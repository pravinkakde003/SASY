package com.sasy.nontag.utils

import android.Manifest

object Constants {
    const val BLUETOOTH_REQUEST_CODE = 101
    const val PERMISSION_BLUETOOTH_REQUEST_CODE = 102
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    const val SELECTED_DEVICE_NAME_KEY = "selected_device_name"
    const val SELECTED_DEVICE_ADDRESS_KEY = "selected_device_address"
}