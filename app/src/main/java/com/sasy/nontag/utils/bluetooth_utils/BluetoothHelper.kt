package com.sasy.nontag.utils.bluetooth_utils

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.IntentFilter
import c.tlgbltcn.library.BluetoothHelperListener

class BluetoothHelper(private val context: Context, private val listener: BluetoothHelperListener) {
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
}