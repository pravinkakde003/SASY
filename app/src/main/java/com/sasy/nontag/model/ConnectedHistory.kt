package com.sasy.nontag.model

data class ConnectedHistory(
    val imageIcon: String = "",
    val deviceName: String = "",
    val device_id: String = ""
)

data class DeviceListDataModel(
    val categoryTitle: String = "",
    val menuList: List<ConnectedHistory>? = null
)