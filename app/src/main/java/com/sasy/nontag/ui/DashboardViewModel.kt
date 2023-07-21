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

    private var _selectedDeviceIcon = MutableLiveData("")
    val selectedDeviceIcon: LiveData<String> = _selectedDeviceIcon

    private val _stateBluetooth = MutableLiveData<BluetoothState<String>?>()
    val stateBluetooth: LiveData<BluetoothState<String>?> = _stateBluetooth

    private val _receivedText = MutableLiveData<String?>()
    val receivedText: LiveData<String?> get() = _receivedText

    private var _isAdminLoggedIn = MutableLiveData(false)
    val isAdminLoggedIn: LiveData<Boolean> get() = _isAdminLoggedIn

    fun setIsAdminLoggedIn(isAdminLoggedIn: Boolean) {
        _isAdminLoggedIn.value = isAdminLoggedIn
    }


    fun setReceivedText(receivedText: String) {
        _receivedText.value = receivedText
    }

    fun resetReceivedText() {
        _receivedText.value = null
    }

    fun setSelectedDeviceName(selectedDeviceName: String) {
        _selectedDeviceName.value = selectedDeviceName
    }

    fun setSelectedDeviceAddress(setSelectedDeviceAddress: String) {
        _selectedDeviceAddress.value = setSelectedDeviceAddress
    }

    fun setSelectedDeviceIcon(setSelectedDeviceIcon: String) {
        _selectedDeviceIcon.value = setSelectedDeviceIcon
    }

    fun setBluetoothState(state: BluetoothState<String>) {
        _stateBluetooth.value = state
    }

    fun isConnected(): Boolean {
        return stateBluetooth.value?.equals(BluetoothState.ConnectedState) == true
    }
}