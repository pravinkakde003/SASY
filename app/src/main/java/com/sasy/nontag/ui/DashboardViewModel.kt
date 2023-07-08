package com.sasy.nontag.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasy.nontag.model.DeviceListDataModel

class DashboardViewModel : ViewModel() {
    val isScanButtonShown = MutableLiveData(false)

    val alreadyPairedDeviceList = ArrayList<DeviceListDataModel>()

    val scannedDeviceList = ArrayList<DeviceListDataModel>()
    var selectedDeviceName = MutableLiveData("")
    var selectedDeviceAddress = MutableLiveData("")

}