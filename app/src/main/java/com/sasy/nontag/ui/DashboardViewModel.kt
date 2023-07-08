package com.sasy.nontag.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasy.nontag.model.DeviceListDataModel

class DashboardViewModel : ViewModel() {
    val isScanButtonShown = MutableLiveData(false)

    val alreadyPairedDeviceList = ArrayList<DeviceListDataModel>()

    val scannedDeviceList = ArrayList<DeviceListDataModel>()

    private var _selectedDeviceName = MutableLiveData("")
    val selectedDeviceName: LiveData<String> = _selectedDeviceName

    private var _selectedDeviceAddress = MutableLiveData("")
    val selectedDeviceAddress: LiveData<String> = _selectedDeviceAddress

    fun setSelectedDeviceName(selectedDeviceName: String) {
        _selectedDeviceName.value = selectedDeviceName
    }

    fun setSelectedDeviceAddress(setSelectedDeviceAddress: String) {
        _selectedDeviceAddress.value = setSelectedDeviceAddress
    }
}