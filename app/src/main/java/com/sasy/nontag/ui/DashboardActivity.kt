package com.sasy.nontag.ui

import android.Manifest
import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import c.tlgbltcn.library.BluetoothHelperListener
import com.sasy.nontag.R
import com.sasy.nontag.model.ConnectedHistory
import com.sasy.nontag.model.DeviceListDataModel
import com.sasy.nontag.ui.adapter.VendorMenuTitleAdapter
import com.sasy.nontag.utils.*
import com.sasy.nontag.utils.bluetooth_utils.BluetoothDeviceMap
import com.sasy.nontag.utils.bluetooth_utils.BluetoothHelper
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.layout_not_bluetooth.*


class DashboardActivity : AppCompatActivity(), BluetoothHelperListener {
    private lateinit var bluetoothHelper: BluetoothHelper
    private var requestedEnable = false

    private val dashboardViewModel: DashboardViewModel by viewModels()


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
            connecting_status_label.text = "Bluetooth is off"
            connecting_status_description.text = "Please turn on your phones bluetooth."
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
            connecting_status_label.text = "Scan & Connect"
            connecting_status_description.text = "Bluetooth State: On"
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
            dashboardViewModel.totalDeviceList.add(
                DeviceListDataModel("Already paired", alreadyPairedDeviceList)
            )

            recyclerview_all_devices.adapter =
                VendorMenuTitleAdapter(this, dashboardViewModel.totalDeviceList) { _, item ->
                    showToast(item.deviceName)
                }

            recyclerview_all_devices.layoutManager = LinearLayoutManager(this)
            recyclerview_all_devices.setHasFixedSize(true)
            recyclerview_all_devices.show()
        } else {
            showToast("No paired device found")
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
            connecting_status_label.text = "Scan & Connect"
            connecting_status_description.text = "Bluetooth State: On"
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
        TODO("Not yet implemented")
    }

    override fun onFinishDiscovery() {
        TODO("Not yet implemented")
    }

    override fun onEnabledBluetooth() {
        setupStatusText()
    }

    override fun onDisabledBluetooh() {
        setupStatusText()
    }

    override fun getBluetoothDeviceList(device: BluetoothDevice?) {
        TODO("Not yet implemented")
    }
}