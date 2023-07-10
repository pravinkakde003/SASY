package com.sasy.nontag.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sasy.nontag.model.DeviceListDataModel
import com.sasy.nontag.utils.bluetooth_utils.BluetoothState

class DashboardViewModel : ViewModel() {
    val isScanButtonShown = MutableLiveData(false)

    val alreadyPairedDeviceList = ArrayList<DeviceListDataModel>()

    val scannedDeviceList = ArrayList<DeviceListDataModel>()

    private var _selectedDeviceName = MutableLiveData("")
    val selectedDeviceName: LiveData<String> = _selectedDeviceName

    private var _selectedDeviceAddress = MutableLiveData("")
    val selectedDeviceAddress: LiveData<String> = _selectedDeviceAddress

    private val _stateBluetooth = MutableLiveData<BluetoothState<String>?>()
    val stateBluetooth: LiveData<BluetoothState<String>?> = _stateBluetooth

    fun setSelectedDeviceName(selectedDeviceName: String) {
        _selectedDeviceName.value = selectedDeviceName
    }

    fun setSelectedDeviceAddress(setSelectedDeviceAddress: String) {
        _selectedDeviceAddress.value = setSelectedDeviceAddress
    }

    fun setBluetoothState(state: BluetoothState<String>) {
        _stateBluetooth.value = state
    }
}