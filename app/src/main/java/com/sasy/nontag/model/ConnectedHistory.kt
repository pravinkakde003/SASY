package com.sasy.nontag.model

data class ConnectedHistory(
    val imageIcon: String = "",
    val deviceName: String = "",
    val device_id: String = "",
    val isPaired: Boolean = false
)

data class DeviceListDataModel(
    val categoryTitle: String = "",
    val menuList: ArrayList<ConnectedHistory>? = null
)