package com.sasy.nontag.ui.fragments

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.databinding.FragmentRangesBinding
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.utils.bluetooth_utils.SerialListener
import com.sasy.nontag.utils.bluetooth_utils.SerialService
import com.sasy.nontag.utils.bluetooth_utils.SerialSocket
import com.sasy.nontag.utils.showToast
import java.util.ArrayDeque


class RangesFragment : Fragment(), ServiceConnection, SerialListener {
    private enum class Connected {
        False, Pending, True
    }

    private lateinit var binding: FragmentRangesBinding
    private val sharedViewModel: DashboardViewModel by activityViewModels()

    private var deviceAddress: String? = null
    private var service: SerialService? = null
    private var connected = Connected.False
    private var initialStart = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRangesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRangesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        deviceAddress = sharedViewModel.selectedDeviceAddress.value

        binding.buttonSetRange.setOnClickListener {
            val currentValue = binding.buttonIncrementDecrement.getCurrentNumber()
            if (currentValue > 0) {
                send(currentValue.toString())
            } else {
                showToast("Please enter range value.")
            }
        }
    }


    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        requireActivity().bindService(
            Intent(getActivity(), SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
    }

    override fun onDetach() {
        try {
            requireActivity().unbindService(this)
        } catch (ignored: Exception) {
        }
        super.onDetach()
    }

    override fun onResume() {
        super.onResume()
        if (initialStart && service != null) {
            initialStart = false
            requireActivity().runOnUiThread { connect() }
        }
    }

    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as SerialService.SerialBinder).service
        service?.attach(this)
        if (initialStart && isResumed) {
            initialStart = false
            requireActivity().runOnUiThread { connect() }
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }

    private fun connect() {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device = bluetoothAdapter.getRemoteDevice(deviceAddress)
            status("connecting...")
            connected = Connected.Pending
            val socket = SerialSocket(requireActivity().applicationContext, device)
            service!!.connect(socket)
        } catch (e: Exception) {
            onSerialConnectError(e)
        }
    }

    private fun disconnect() {
        connected = Connected.False
        service?.disconnect()
    }

    private fun send(str: String) {
        if (connected != Connected.True) {
            Toast.makeText(activity, "not connected", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            val message = "SET XRANGE $str"
            val data: ByteArray = message.toByteArray()
            service?.write(data)
        } catch (e: Exception) {
            onSerialIoError(e)
        }
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        requireActivity().stopService(Intent(activity, SerialService::class.java))
        super.onDestroy()
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service?.attach(this) else requireActivity().startService(
            Intent(
                activity,
                SerialService::class.java
            )
        ) // prevents service destroy on unbind from recreated activity caused by orientation change
    }

    override fun onStop() {
        if (service != null && !requireActivity().isChangingConfigurations) service!!.detach()
        super.onStop()
    }

    override fun onSerialConnect() {
        status("connected")
        connected = Connected.True
    }

    override fun onSerialConnectError(e: Exception) {
        status("connection failed: " + e.message)
        disconnect()
    }

    override fun onSerialRead(data: ByteArray) {
        val datas = ArrayDeque<ByteArray>()
        datas.add(data)
        receive(datas)
    }

    override fun onSerialRead(datas: ArrayDeque<ByteArray>) {
        receive(datas)
    }

    override fun onSerialIoError(e: Exception?) {
        status("connection lost: " + e?.message)
        disconnect()
    }

    private fun status(str: String) {
        showToast(str)
    }

    private fun receive(datas: ArrayDeque<ByteArray>) {
        for (data in datas) {
            var msg = String(data)
            if (msg.isNotEmpty()) {
                showToast(msg)
            }
        }
    }
}