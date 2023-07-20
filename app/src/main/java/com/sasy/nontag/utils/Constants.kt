package com.sasy.nontag.utils

import android.Manifest

object Constants {
    const val BLUETOOTH_REQUEST_CODE = 101
    const val PERMISSION_BLUETOOTH_REQUEST_CODE = 102

    const val TOAST_DELAY = 1000L
    val REQUIRED_PERMISSIONS = arrayOf(
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT,
        Manifest.permission.ACCESS_FINE_LOCATION,
    )

    const val SELECTED_DEVICE_NAME_KEY = "selected_device_name"
    const val SELECTED_DEVICE_ADDRESS_KEY = "selected_device_address"
    const val SELECTED_DEVICE_ICON_TYPE = "selected_device_icon"

    const val INTENT_ACTION_DISCONNECT: String = "com.sasy.nontag.Disconnect"
    const val NOTIFICATION_CHANNEL: String = "com.sasy.nontag.Channel"
    const val INTENT_CLASS_MAIN_ACTIVITY: String = "com.sasy.nontag.ui.DetailActivity"

    // values have to be unique within each app
    const val NOTIFY_MANAGER_START_FOREGROUND_SERVICE = 1001

    // DATA Constants
    const val CARRIAGE = "\r"
    const val SET_XRANGE = "SET XRANGE"
    const val GET_XRANGE = "GET XRANGE"

    const val SET_BLESIG = "SET BLESIG"
    const val GET_BLESIG = "GET BLESIG"

    const val SET_XDB = "SET XDB"
    const val SET_RID = "SET RID"
    const val GET_RID = "GET RID"
    const val SET_RTC = "SET RTC"

    const val CLEAR_LOG = "SET DEFLFILE"
    const val SET_DEFAULT_LOG = "SET DEFPFILE"

    const val SET_VELOCITY = "SET VELOCITY"
    const val GET_VELOCITY = "GET VELOCITY"
    const val COPY_FIRMWARE = "SET CPYUSBFIRM"


    const val ADD_TAG = "SET ADDTAG"
    const val DELETE_TAG = "SET DELTAG"
    const val GET_TAG_LIST = "GET TAGLIST"

    const val BLE_MODE_ON_OFF = "SET BLEMOD"

    const val BLE_FRONT_CAMERA_ENABLE_DISABLE = "SET CFRONT"
    const val BLE_BACK_CAMERA_ENABLE_DISABLE = "SET CBACK"
    const val BLE_LEFT_CAMERA_ENABLE_DISABLE = "SET CLEFT"
    const val BLE_RIGHT_CAMERA_ENABLE_DISABLE = "SET CRIGHT"

    const val DEVICE_INFO = "GET SYSPARAM"
}