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
    const val SELECTED_DEVICE_ICON_TYPE = "selected_device_icon"

    const val INTENT_ACTION_DISCONNECT: String = "com.sasy.nontag.utils.Disconnect"
    const val NOTIFICATION_CHANNEL: String = "com.sasy.nontag.utils.Channel"
    const val INTENT_CLASS_MAIN_ACTIVITY: String = "com.sasy.nontag.utils.MainActivity"

    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001

    // DATA Constants
    const val CARRIAGE = "\n"
    const val SET_XRANGE = "SET XRANGE"
    const val SET_DB = "SET DB"
    const val SET_RTC = "SET RTC"
    const val SET_RID = "SET RID"
    const val DEVICE_INFO = "GET SYSPARAM"

    const val GET_XRANGE = "GET XRANGE"
}