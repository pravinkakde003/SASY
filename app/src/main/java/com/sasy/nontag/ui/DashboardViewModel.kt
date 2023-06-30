package com.sasy.nontag.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasy.nontag.model.DeviceListDataModel

class DashboardViewModel : ViewModel() {
    var totalDeviceList: ArrayList<DeviceListDataModel> = ArrayList()
    val isScanButtonShown = MutableLiveData(false)
}