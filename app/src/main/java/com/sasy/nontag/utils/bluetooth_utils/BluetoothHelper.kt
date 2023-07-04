package com.sasy.nontag.utils.bluetooth_utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Handler
import android.os.Looper

class BluetoothHelper(private val context: Context, private val listener: BluetoothHelperListener) {
    private var handler: Handler? = null
    private val mBluetoothAdapter by lazy {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter

        bluetoothManager.adapter?.let {
            return@lazy it
        } ?: run {
            throw RuntimeException(
                "Bluetooth is not supported on this hardware platform. " +
                        "Make sure you try it from the real device\n " +
                        "You could more information from here:\n" +
                        "https://developer.android.com/reference/android/bluetooth/BluetoothAdapter"
            )
        }
    }

    fun create(): BluetoothHelper {
        handler = Handler(Looper.getMainLooper())
        return BluetoothHelper(context, listener)
    }

    private var isEnabled = mBluetoothAdapter.isEnabled
    fun isBluetoothEnabled() = isEnabled

    fun registerBluetoothStateChanged() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        context.registerReceiver(mBluetoothStateChangeReceiver, intentFilter)
    }

    fun unregisterBluetoothStateChanged() {
        context.unregisterReceiver(mBluetoothStateChangeReceiver)
    }

    fun getBluetoothAdapter(): BluetoothAdapter {
        return mBluetoothAdapter
    }

    private val mBluetoothStateChangeReceiver by lazy {
        object : BluetoothStateChangeReceiver() {
            override fun onStartDiscovering() {
                listener.onStartDiscovery()
            }

            override fun onFinishDiscovering() {
                listener.onFinishDiscovery()
            }

            override fun onEnabledBluetooth() {
                isEnabled = true
                listener.onEnabledBluetooth()
            }

            override fun onDisabledBluetooth() {
                isEnabled = false
                listener.onDisabledBluetooh()
            }
        }
    }


    fun startDetectNearbyDevices() {
        handler?.removeCallbacks(runnable)
        startBluetoothDiscovery()
        handler?.postDelayed(runnable, 500)
    }

    private val runnable = Runnable { startBluetoothDiscovery() }

    private fun startBluetoothDiscovery() {
        try {
            registerDiscoveryBroadcast(true)
            mBluetoothAdapter.startDiscovery()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private var isRegisterDiscoveryBroadcast = false
    private fun registerDiscoveryBroadcast(isRegister: Boolean) {
        if (isRegister) {
            if (!isRegisterDiscoveryBroadcast) {
                // register
                val intentFilter = IntentFilter()
                intentFilter.addAction(BluetoothDevice.ACTION_FOUND)
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
                intentFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
                context.registerReceiver(discoveryBroadcast, intentFilter)
                isRegisterDiscoveryBroadcast = true
            }
        } else {
            if (isRegisterDiscoveryBroadcast) {
                // unregister
                context.unregisterReceiver(discoveryBroadcast)
                isRegisterDiscoveryBroadcast = false
            }
        }
    }

    private var onDetectNearbyDeviceListener: BluetoothHelperListener.onDetectNearbyDeviceListener? =
        null
    private var onDiscoveryStateChangedListener: BluetoothHelperListener.onDiscoveryStateChangedListener? =
        null

    fun setOnDetectNearbyDeviceListener(onDetectNearbyDeviceListener: BluetoothHelperListener.onDetectNearbyDeviceListener?) {
        this.onDetectNearbyDeviceListener = onDetectNearbyDeviceListener
    }

    //Constant Values
    val DISCOVERY_STARTED = 113
    val DISCOVERY_FINISHED = 114

    // discovery device broadcast receiver
    private val discoveryBroadcast: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (action != null && action != "") {
                if (action == BluetoothDevice.ACTION_FOUND) {
                    //Device found
                    val device =
                        intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
                    if (device != null && device.name != null && device.address != null) {
                        // Check device is paired or not
                        if (device.bondState != BluetoothDevice.BOND_BONDED) {
                            onDetectNearbyDeviceListener?.onDeviceDetected(device)
                        }
                    }
                }
                if (action == BluetoothAdapter.ACTION_DISCOVERY_STARTED) {
                    //Device discovry started
                    onDiscoveryStateChangedListener?.onDiscoveryStateChanged(DISCOVERY_STARTED)
                }
                if (action == BluetoothAdapter.ACTION_DISCOVERY_FINISHED) {
                    registerDiscoveryBroadcast(false)
                    //Device discovery finished
                    onDiscoveryStateChangedListener?.onDiscoveryStateChanged(DISCOVERY_FINISHED)
                }
            }
        }
    }
}