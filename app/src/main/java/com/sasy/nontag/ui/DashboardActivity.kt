package com.sasy.nontag.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.model.ConnectedHistory
import com.sasy.nontag.model.DeviceListDataModel
import com.sasy.nontag.ui.adapter.DeviceListTitleAdapter
import com.sasy.nontag.utils.*
import com.sasy.nontag.utils.bluetooth_utils.BluetoothDeviceMap
import com.sasy.nontag.utils.bluetooth_utils.BluetoothHelper
import com.sasy.nontag.utils.bluetooth_utils.BluetoothHelperListener
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.layout_not_bluetooth.*


class DashboardActivity : AppCompatActivity(), BluetoothHelperListener {
    private lateinit var bluetoothHelper: BluetoothHelper
    private var requestedEnable = false
    private val dashboardViewModel: DashboardViewModel by viewModels()
    val scannedDevices = mutableSetOf<BluetoothDevice>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        bluetoothHelper = BluetoothHelper(this@DashboardActivity, this).create()
        checkPermissions()
    }

    private fun setonClickListeners() {
        buttonTurnOnBluetooth.setOnClickListener {
            checkBluetoothSupported()
        }
        button_view_details.setOnClickListener {
            launchActivity<DetailActivity> { }
        }
        buttonInitiateScan.setOnClickListener {
            bluetoothHelper.startDetectNearbyDevices()
        }
    }

    private fun checkPermissions() {
        if (hasPermissions()) {
            initiateApplicationFlow()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                requestMultiplePermissions.launch(
                    Constants.REQUIRED_PERMISSIONS
                )
            } else {
                initiateApplicationFlow()
            }
        }
    }

    private fun initiateApplicationFlow() {
        setonClickListeners()
        setupStatusText()
        bluetoothHelper.setOnDetectNearbyDeviceListener(object :
            BluetoothHelperListener.onDetectNearbyDeviceListener {
            @SuppressLint("MissingPermission")
            override fun onDeviceDetected(device: BluetoothDevice?) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    device?.let { scannedDevices.add(it) }
                } else {
                    device?.let { scannedDevices.add(it) }
                }
            }
        })
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val granted = permissions.entries.all {
                    it.value == true
                }
                if (granted) {
                    initiateApplicationFlow()
                } else {
                    checkPermissions()
                }
            }
        }

    private fun setupStatusText() {
        if (!bluetoothHelper.isBluetoothEnabled()) {
            connecting_status_label.text = getString(R.string.bluetooth_is_off)
            connecting_status_description.text = getString(R.string.please_turn_on_bluetooth)
            imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_disabled_24
                )
            )
            imageViewBluetoothState.setTint(R.color.black_overlay)
            buttonTurnOnBluetooth.show()
            buttonInitiateScan.hide()
        } else {
            connecting_status_label.text = getString(R.string.scan_and_connect)
            connecting_status_description.text = getString(R.string.bluetooth_state_on)
            imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_24
                )
            )
            imageViewBluetoothState.setTint(R.color.app_primary_color)
            buttonTurnOnBluetooth.hide()
            buttonInitiateScan.show()
            getPairedDeviceList()
        }
    }

    private fun getPairedDeviceList() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                setPairedRecyclerview()
            }
        } else {
            setPairedRecyclerview()
        }
    }

    @SuppressLint("MissingPermission")
    private fun setPairedRecyclerview() {
        val alreadyPairedDeviceList = mutableListOf<ConnectedHistory>()
        val pairedDevices: Set<BluetoothDevice>? =
            bluetoothHelper.getBluetoothAdapter().bondedDevices
        pairedDevices?.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
            val deviceType = device.bluetoothClass.deviceClass
            val drawable: Int =
                BluetoothDeviceMap().getDrawable(deviceType)
            alreadyPairedDeviceList.add(
                ConnectedHistory(
                    drawable.toString(),
                    deviceName,
                    deviceHardwareAddress
                )
            )
        }

        if (alreadyPairedDeviceList.isNotEmpty()) {
            dashboardViewModel.alreadyPairedDeviceList.add(
                DeviceListDataModel(
                    getString(R.string.already_paired),
                    alreadyPairedDeviceList
                )
            )
            recyclerviewAlreadyPaired.layoutManager = LinearLayoutManager(this)
            recyclerviewAlreadyPaired.setHasFixedSize(true)
            val pairedListAdapter = DeviceListTitleAdapter(
                this,
                dashboardViewModel.alreadyPairedDeviceList
            ) { _, item ->
                showToast(item.deviceName)
            }
            recyclerviewAlreadyPaired.adapter = pairedListAdapter
            recyclerviewAlreadyPaired.show()
        } else {
            showToast(getString(R.string.no_paired_device_found))
        }
    }

    private fun checkBluetoothSupported() {
        if (isBluetoothAvailable()) {
            if (!bluetoothHelper.isBluetoothEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bleRequestEnable.launch(enableBtIntent)
            }
        } else {
            alert {
                setTitle(getString(R.string.error))
                setMessage(getString(R.string.no_bluetooth_support))
                positiveButton(getString(R.string.ok)) {
                    finish()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        bluetoothHelper.registerBluetoothStateChanged()
    }

    override fun onStop() {
        super.onStop()
        bluetoothHelper.unregisterBluetoothStateChanged()
    }

    private val bleRequestEnable = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            requestedEnable = false
            connecting_status_label.text = getString(R.string.scan_and_connect)
            connecting_status_description.text = getString(R.string.bluetooth_state_on)
            imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_24
                )
            )
            imageViewBluetoothState.setTint(R.color.app_primary_color)
            buttonTurnOnBluetooth.hide()
            buttonInitiateScan.show()
            getPairedDeviceList()
        } else {
            showToast(permissionMissing)
        }
    }

    override fun onStartDiscovery() {
        Log.e("TAGG", "onStartDiscovery")
        imageViewBluetoothState.hide()
        buttonInitiateScan.hide()
        pulseLayout.show()
        pulseLayout.startPulse()
    }

    @SuppressLint("MissingPermission")
    override fun onFinishDiscovery() {
        pulseLayout.stopPulse()
        pulseLayout.hide()
        imageViewBluetoothState.show()
        buttonInitiateScan.show()
        Log.e("TAGG", "onFinishDiscovery")
        val newScannedDeviceList = mutableListOf<ConnectedHistory>()

        scannedDevices.forEach { device ->
            val deviceName = device.name
            val deviceHardwareAddress = device.address
            val deviceType = device.bluetoothClass.deviceClass
            val drawable: Int =
                BluetoothDeviceMap().getDrawable(deviceType)
            newScannedDeviceList.add(
                ConnectedHistory(
                    drawable.toString(),
                    deviceName,
                    deviceHardwareAddress
                )
            )
        }

        if (newScannedDeviceList.isNotEmpty()) {
            dashboardViewModel.scannedDeviceList.add(
                DeviceListDataModel(
                    getString(R.string.avaialbe_device_title),
                    newScannedDeviceList
                )
            )
            recyclerviewScannedDevices.layoutManager = LinearLayoutManager(this)
            recyclerviewScannedDevices.setHasFixedSize(true)
            val scannedDeviceListAdapter = DeviceListTitleAdapter(
                this,
                dashboardViewModel.scannedDeviceList
            ) { _, item ->
                showToast(item.deviceName)
            }
            recyclerviewScannedDevices.adapter = scannedDeviceListAdapter
            recyclerviewScannedDevices.show()
        } else {
            showToast(getString(R.string.no_scan_device_found))
        }
    }

    override fun onEnabledBluetooth() {
        setupStatusText()
    }

    override fun onDisabledBluetooh() {
        setupStatusText()
    }
}