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
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.HapticFeedbackConstants
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.databinding.ActivityDashboardBinding
import com.sasy.nontag.model.ConnectedHistory
import com.sasy.nontag.ui.adapter.DeviceListAdapter
import com.sasy.nontag.utils.*
import com.sasy.nontag.utils.bluetooth_utils.BluetoothConnectionReceiver
import com.sasy.nontag.utils.bluetooth_utils.BluetoothDeviceMap
import com.sasy.nontag.utils.bluetooth_utils.BluetoothHelper
import com.sasy.nontag.utils.bluetooth_utils.BluetoothHelperListener


class DashboardActivity : AppCompatActivity(), BluetoothHelperListener,
    BluetoothHelperListener.bluetoothParingListner {
    private lateinit var binding: ActivityDashboardBinding
    private lateinit var bluetoothHelper: BluetoothHelper
    private var requestedEnable = false
    private val dashboardViewModel: DashboardViewModel by viewModels()
    val scannedDevices = mutableSetOf<BluetoothDevice>()
    lateinit var selectedDeviceToBond: ConnectedHistory

    private lateinit var scannedDeviceListAdapter: DeviceListAdapter
    private lateinit var pairedListAdapter: DeviceListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bluetoothHelper = BluetoothHelper(this@DashboardActivity, this).create()
        checkPermissions()
        BluetoothConnectionReceiver().apply {
            register(this@DashboardActivity)
            setBluetoothPairingReceiverListener(this@DashboardActivity)
        }
    }

    private fun setonClickListeners() {
        binding.layoutNoBluetooth.buttonTurnOnBluetooth.setOnClickListener {
            checkBluetoothSupported()
        }
        binding.buttonViewDetails.setOnClickListener {
            launchActivity<DetailActivity> { }
        }
        binding.layoutNoBluetooth.buttonInitiateScan.setOnClickListener {
            if (this::scannedDeviceListAdapter.isInitialized) {
                dashboardViewModel.scannedDeviceList.clear()
                scannedDeviceListAdapter.clearData()
                binding.textAvailableDevices.hide()
            }
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
            binding.connectingStatusLabel.text = getString(R.string.bluetooth_is_off)
            binding.connectingStatusDescription.text = getString(R.string.please_turn_on_bluetooth)
            binding.layoutNoBluetooth.imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_disabled_24
                )
            )
            binding.layoutNoBluetooth.imageViewBluetoothState.setTint(R.color.black_overlay)
            binding.layoutNoBluetooth.buttonTurnOnBluetooth.show()
            binding.layoutNoBluetooth.buttonInitiateScan.hide()
        } else {
            binding.connectingStatusLabel.text = getString(R.string.scan_and_connect)
            binding.connectingStatusDescription.text = getString(R.string.bluetooth_state_on)
            binding.layoutNoBluetooth.imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_24
                )
            )
            binding.layoutNoBluetooth.imageViewBluetoothState.setTint(R.color.app_primary_color)
            binding.layoutNoBluetooth.buttonTurnOnBluetooth.hide()
            binding.layoutNoBluetooth.buttonInitiateScan.show()
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
        dashboardViewModel.alreadyPairedDeviceList.clear()
        val alreadyPairedDeviceList = ArrayList<ConnectedHistory>()
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
                    imageIcon = drawable.toString(),
                    deviceName = deviceName,
                    device_id = deviceHardwareAddress,
                    isPaired = true
                )
            )
        }

        if (alreadyPairedDeviceList.isNotEmpty()) {
            dashboardViewModel.alreadyPairedDeviceList.addAll(alreadyPairedDeviceList)
            binding.recyclerviewAlreadyPaired.addItemDecoration(
                DividerItemDecoration(
                    this@DashboardActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            binding.recyclerviewAlreadyPaired.layoutManager = LinearLayoutManager(this)

            binding.recyclerviewAlreadyPaired.setHasFixedSize(true)
            pairedListAdapter = DeviceListAdapter(
                dashboardViewModel.alreadyPairedDeviceList,
                this,
            ) { view, item ->
                if (view is AppCompatImageView) {
                    view.setHapticFeedbackEnabled(true)
                    view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                    alert {
                        setTitle(getString(R.string.information))
                        setMessage(getString(R.string.unpair_device))
                        positiveButton(getString(R.string.ok)) {
                            val device = bluetoothHelper.getBluetoothAdapter()
                                .getRemoteDevice(item.device_id)
                            val pair = device.javaClass.getMethod("removeBond")
                            pair.invoke(device)
                            dashboardViewModel.alreadyPairedDeviceList.removeIf { it.device_id == device.address }
                            binding.recyclerviewAlreadyPaired.adapter?.notifyDataSetChanged()
                            Handler(Looper.getMainLooper()).postDelayed({
                                setPairedRecyclerview()
                            }, 600)
                        }
                        negativeButton(getString(R.string.cancel)) {

                        }
                    }
                } else {
                    dashboardViewModel.setSelectedDeviceName(item.deviceName)
                    dashboardViewModel.setSelectedDeviceAddress(item.device_id)
                    launchActivity<DetailActivity> {
                        putExtra(Constants.SELECTED_DEVICE_NAME_KEY, item.deviceName)
                        putExtra(Constants.SELECTED_DEVICE_ADDRESS_KEY, item.device_id)
                        putExtra(Constants.SELECTED_DEVICE_ICON_TYPE, item.imageIcon)
                    }
                }
            }
            binding.recyclerviewAlreadyPaired.adapter = pairedListAdapter
            binding.recyclerviewAlreadyPaired.show()
            binding.textAlreadyPaired.show()
        } else {
            binding.textAlreadyPaired.hide()
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
        BluetoothConnectionReceiver().unRegisterReceiver(this)
        bluetoothHelper.registerBluetoothStateChanged()
    }

    private val bleRequestEnable = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == Activity.RESULT_OK) {
            requestedEnable = false
            binding.connectingStatusLabel.text = getString(R.string.scan_and_connect)
            binding.connectingStatusDescription.text = getString(R.string.bluetooth_state_on)
            binding.layoutNoBluetooth.imageViewBluetoothState.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_baseline_bluetooth_24
                )
            )
            binding.layoutNoBluetooth.imageViewBluetoothState.setTint(R.color.app_primary_color)
            binding.layoutNoBluetooth.buttonTurnOnBluetooth.hide()
            binding.layoutNoBluetooth.buttonInitiateScan.show()
            getPairedDeviceList()
        } else {
            showToast(permissionMissing)
        }
    }

    override fun onStartDiscovery() {
        Log.e("TAGG", "onStartDiscovery")
        binding.layoutNoBluetooth.imageViewBluetoothState.hide()
        binding.layoutNoBluetooth.buttonInitiateScan.hide()
        binding.pulseLayout.show()
        binding.pulseLayout.startPulse()
    }

    @SuppressLint("MissingPermission")
    override fun onFinishDiscovery() {
        binding.pulseLayout.stopPulse()
        binding.pulseLayout.hide()
        binding.layoutNoBluetooth.imageViewBluetoothState.show()
        binding.layoutNoBluetooth.buttonInitiateScan.show()
        Log.e("TAGG", "onFinishDiscovery")
        val newScannedDeviceList = ArrayList<ConnectedHistory>()
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
        newScannedDeviceList.filter { it.device_id in dashboardViewModel.scannedDeviceList.map { item -> item.device_id } }

        if (newScannedDeviceList.isNotEmpty()) {
            binding.textAvailableDevices.show()
            dashboardViewModel.scannedDeviceList.addAll(newScannedDeviceList)
            binding.recyclerviewScannedDevices.addItemDecoration(
                DividerItemDecoration(
                    this@DashboardActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
            binding.recyclerviewScannedDevices.layoutManager = LinearLayoutManager(this)
            binding.recyclerviewScannedDevices.setHasFixedSize(true)
            scannedDeviceListAdapter = DeviceListAdapter(
                dashboardViewModel.scannedDeviceList,
                this,
            ) { _, item ->
                createBond(item)
            }
            binding.recyclerviewScannedDevices.adapter = scannedDeviceListAdapter
            binding.recyclerviewScannedDevices.show()
        } else {
            binding.textAvailableDevices.hide()
            showToast(getString(R.string.no_scan_device_found))
        }
    }

    override fun onEnabledBluetooth() {
        setupStatusText()
    }

    override fun onDisabledBluetooh() {
        setupStatusText()
    }

    private fun createBond(item: ConnectedHistory) {
        selectedDeviceToBond = item
        callCreateBond()
    }


    @SuppressLint("MissingPermission")
    fun callCreateBond() {
        bluetoothHelper.getBluetoothAdapter()
            .getRemoteDevice(selectedDeviceToBond.device_id)
            .createBond()
    }

    @SuppressLint("MissingPermission")
    override fun onPairSuccess(device: BluetoothDevice) {
        dashboardViewModel.scannedDeviceList.removeIf { it.device_id == device.address }
        if (dashboardViewModel.scannedDeviceList.size != 0) {
            binding.textAvailableDevices.show()
        } else {
            binding.textAvailableDevices.hide()
        }
        val deviceName = device.name
        val deviceHardwareAddress = device.address
        val deviceType = device.bluetoothClass.deviceClass
        val drawable: Int =
            BluetoothDeviceMap().getDrawable(deviceType)
        dashboardViewModel.alreadyPairedDeviceList.add(
            ConnectedHistory(
                drawable.toString(),
                deviceName,
                deviceHardwareAddress
            )
        )
        binding.recyclerviewScannedDevices.adapter?.notifyDataSetChanged()
        Handler(Looper.getMainLooper()).postDelayed({
            setPairedRecyclerview()
        }, 600)
    }

    override fun onPairFailed(rootCause: String) {
//        showToast(rootCause)
    }

    override fun onStop() {
        super.onStop()
        bluetoothHelper.unregisterBluetoothStateChanged()
    }
}