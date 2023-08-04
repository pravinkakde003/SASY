package com.sasy.nontag.utils.bluetooth_utils

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import com.sasy.nontag.R

class BluetoothConnectionReceiver : BroadcastReceiver() {
    var isRegistered = false

    private var listener: BluetoothHelperListener.bluetoothParingListner? = null

    fun setBluetoothPairingReceiverListener(listener: BluetoothHelperListener.bluetoothParingListner?) {
        this.listener = listener
    }

    @SuppressLint("MissingPermission")
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val device = intent.getParcelableExtra<BluetoothDevice>(BluetoothDevice.EXTRA_DEVICE)
        val state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR)
        val prevState =
            intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR)
        when {
            state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING -> {
                device?.let { listener?.onPairSuccess(it) }
            }
            state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED -> {
                listener?.onPairFailed(context.getString(R.string.request_rejected))
            }
            BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED == action -> {
                listener?.onPairFailed(context.getString(R.string.request_rejected))
            }
            BluetoothDevice.ACTION_ACL_DISCONNECTED == action -> {
                listener?.onPairFailed(context.getString(R.string.request_rejected))
            }
        }
    }

    fun register(context: Context) {
        val filter = IntentFilter()
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED)
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED)
        context.registerReceiver(this, filter)
        isRegistered = true
    }

    fun unRegisterReceiver(context: Context): Boolean {
        return (isRegistered
                && unregisterInternal(context))
    }

    private fun unregisterInternal(context: Context): Boolean {
        context.unregisterReceiver(this)
        isRegistered = false
        return true
    }
}