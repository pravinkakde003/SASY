package com.sasy.nontag.ui.activity

import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.databinding.ActivityDetailsBinding
import com.sasy.nontag.ui.adapter.MenuAdapter
import com.sasy.nontag.ui.fragments.*
import com.sasy.nontag.ui.viewmodel.DashboardViewModel
import com.sasy.nontag.utils.*
import com.sasy.nontag.utils.bluetooth_utils.BluetoothState
import com.sasy.nontag.utils.bluetooth_utils.SerialListener
import com.sasy.nontag.utils.bluetooth_utils.SerialService
import com.sasy.nontag.utils.bluetooth_utils.SerialSocket
import java.util.*


class DetailActivity : AppCompatActivity(), ServiceConnection, SerialListener {
    private enum class Connected {
        False, Pending, True
    }

    enum class Status {
        Success, Error
    }

    private var service: SerialService? = null
    private var connected = Connected.False
    private var initialStart = true
    private var isResumed = true

    private lateinit var binding: ActivityDetailsBinding

    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        intent?.let {
            intent.getStringExtra(Constants.SELECTED_DEVICE_NAME_KEY)
                ?.let { deviceName -> dashboardViewModel.setSelectedDeviceName(deviceName) }
            intent.getStringExtra(Constants.SELECTED_DEVICE_ADDRESS_KEY)
                ?.let { deviceAddress -> dashboardViewModel.setSelectedDeviceAddress(deviceAddress) }
            intent.getStringExtra(Constants.SELECTED_DEVICE_ICON_TYPE)
                ?.let { deviceIcon -> dashboardViewModel.setSelectedDeviceIcon(deviceIcon) }
            intent.getStringExtra(Constants.USER_TYPE)
                ?.let { userType ->
                    if (userType == Constants.ADMIN) {
                        setupAdminAccount()
                    } else if (userType == Constants.USER) {
                        setupUserAccount()
                    }
                }
        }
        bindService(
            Intent(this, SerialService::class.java),
            this,
            Context.BIND_AUTO_CREATE
        )
        setupToolbar()
    }

    override fun onStart() {
        super.onStart()
        if (service != null) service?.attach(this) else startService(
            Intent(
                this@DetailActivity,
                SerialService::class.java
            )
        )
    }

    override fun onResume() {
        super.onResume()
        if (initialStart && service != null) {
            initialStart = false
            isResumed = false
            runOnUiThread { connect() }
            dashboardViewModel.setBluetoothState(BluetoothState.ConnectingState)
        }
    }

    override fun onStop() {
        try {
            if (service != null && !isChangingConfigurations) service?.detach()
            unbindService(this)
        } catch (ignored: Exception) {
        }
        super.onStop()
    }

    override fun onDestroy() {
        if (connected != Connected.False) disconnect()
        stopService(Intent(this@DetailActivity, SerialService::class.java))
        super.onDestroy()
    }

    fun send(message: String) {
        if (connected != Connected.True) {
            // showToast("not connected")
            dashboardViewModel.setBluetoothState(BluetoothState.Error(resources.getString(R.string.not_connected)))
            return
        }
        try {
            val data: ByteArray = message.toByteArray()
            service?.write(data)
        } catch (e: Exception) {
            onSerialIoError(e)
        }
    }


    override fun onServiceConnected(name: ComponentName, binder: IBinder) {
        service = (binder as SerialService.SerialBinder).service
        service?.attach(this)
        if (initialStart && isResumed) {
            initialStart = false
            isResumed = false
            runOnUiThread { connect() }
        }
    }

    override fun onServiceDisconnected(name: ComponentName) {
        service = null
    }

    private fun connect() {
        try {
            val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            val device =
                bluetoothAdapter.getRemoteDevice(dashboardViewModel.selectedDeviceAddress.value)
            status("connecting...")
            dashboardViewModel.setBluetoothState(BluetoothState.ConnectingState)
            connected = Connected.Pending
            val socket = SerialSocket(applicationContext, device)
            service?.connect(socket)
        } catch (e: Exception) {
            onSerialConnectError(e)
        }
    }

    private fun disconnect() {
        connected = Connected.False
        service?.disconnect()
    }

    override fun onSerialConnect() {
        status("connected")
        dashboardViewModel.setBluetoothState(BluetoothState.ConnectedState)
        connected = Connected.True
    }

    override fun onSerialConnectError(e: Exception) {
        status("connection failed: " + e.message)
//        dashboardViewModel.setBluetoothState(BluetoothState.Error("Connection failed: " + e?.message))
        dashboardViewModel.setBluetoothState(BluetoothState.Error("Connection failed"))
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
//        dashboardViewModel.setBluetoothState(BluetoothState.Error("Connection lost: " + e?.message))
        dashboardViewModel.setBluetoothState(BluetoothState.Error("Connection lost"))
        disconnect()
    }

    private fun status(str: String) {
//        showToast(str)
    }

    private fun receive(datas: ArrayDeque<ByteArray>) {
        for (data in datas) {
            val msg = String(data)
            if (msg.isNotEmpty()) {
                dashboardViewModel.setReceivedText(msg)
            }
        }
    }

    private fun observeState() {
        dashboardViewModel.stateBluetooth.observe(
            this
        ) { uiState ->
            when (uiState) {
                is BluetoothState.ConnectingState -> {
                    val resourceId: Int =
                        resources.getIdentifier(
                            dashboardViewModel.selectedDeviceIcon.value,
                            "drawable",
                            packageName
                        )
                    if (resourceId != 0) {
                        binding.imageViewDeviceType.setImageResource(resourceId)
                    }
                    binding.textViewDeviceName.text =
                        dashboardViewModel.selectedDeviceName.value
                    binding.textViewDeviceAddress.text =
                        dashboardViewModel.selectedDeviceAddress.value
                    binding.textViewStatus.text = getString(R.string.connecting)
                    binding.textViewStatus.setTextColorRes(R.color.colorLightGray)
                }

                is BluetoothState.ConnectedState -> {
                    Handler(Looper.getMainLooper()).postDelayed({
                        Log.e("TAGG", "ConnectedState")
                        binding.textViewDeviceName.text =
                            dashboardViewModel.selectedDeviceName.value
                        binding.textViewDeviceAddress.text =
                            dashboardViewModel.selectedDeviceAddress.value
                        binding.textViewStatus.text = getString(R.string.connected)
                        binding.textViewStatus.setTextColorRes(R.color.app_primary_color)
                    }, 2000)
                }

                is BluetoothState.Error -> {
                    Log.e("TAGG", "Error")
                    binding.textViewDeviceName.text =
                        dashboardViewModel.selectedDeviceName.value
                    binding.textViewDeviceAddress.text =
                        dashboardViewModel.selectedDeviceAddress.value
                    binding.textViewStatus.text = uiState.errorMessage
                    binding.textViewStatus.setTextColorRes(R.color.error_color)
                }

                else -> {
                    binding.textViewDeviceName.text =
                        dashboardViewModel.selectedDeviceName.value
                    binding.textViewDeviceAddress.text =
                        dashboardViewModel.selectedDeviceAddress.value
                    binding.textViewStatus.text =
                        getString(R.string.something_went_wrong)
                    binding.textViewStatus.setTextColorRes(R.color.error_color)
                }
            }
        }
    }

    private fun setupToolbar() {
        binding.toolbar.backArrowImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setupAdminAccount() {
        dashboardViewModel.setIsAdminLoggedIn(true)
        binding.toolbar.txtUserAccount.text = getString(R.string.admin)
        setAdminMenuRecyclerView()
    }

    private fun setupUserAccount() {
        binding.toolbar.txtUserAccount.text = getString(R.string.user)
        dashboardViewModel.setIsAdminLoggedIn(false)
        setUserMenuRecyclerView()
    }


    override fun onBackPressed() {
        if (dashboardViewModel.isAdminLoggedIn.value == true) {
            alert {
                setTitle(getString(R.string.information))
                setMessage(getString(R.string.admin_logout_msg))
                positiveButton(getString(R.string.ok)) {
                    finish()
                }
                negativeButton(getString(R.string.cancel)) {

                }
            }
        } else {
            finish()
        }
    }

    private fun setToolbarTitle(toolbarTitle: String) {
        binding.toolbar.txtDashboardTitle.text = toolbarTitle
    }

    private fun setUserMenuRecyclerView() {
        val menuList =
            AppUtils.getMenuList(AppUtils.getArrayListFromJson(this, R.raw.user_menu_item))
        binding.recyclerviewDetails.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerviewDetails.setHasFixedSize(true)
        setToolbarTitle(getString(R.string.device_id))
        replaceFragment(RidFragment())
        observeState()
        val mAdapter = MenuAdapter(this, menuList) { _, item ->
            setToolbarTitle(item.name)
            when (item.id) {
                1 -> {
                    replaceFragment(RidFragment())
                }
                2 -> {
                    replaceFragment(DevInfoFragment())
                }
                3 -> {
                    replaceFragment(RangesFragment())
                }
                4 -> {
                    replaceFragment(DeadBandFragment())
                }
                5 -> {
                    replaceFragment(SyncRTFragment())
                }
                6 -> {
                    replaceFragment(AddDeleteTagFragment())
                }
                7 -> {
                    replaceFragment(TagListFragment())
                }
                8 -> {
                    replaceFragment(VelocityFragment())
                }
            }
        }
        binding.recyclerviewDetails.adapter = mAdapter
    }

    private fun setAdminMenuRecyclerView() {
        val menuList =
            AppUtils.getMenuList(AppUtils.getArrayListFromJson(this, R.raw.menu_item))
        binding.recyclerviewDetails.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerviewDetails.setHasFixedSize(true)
        setToolbarTitle(getString(R.string.device_id))
        replaceFragment(RidFragment())
        observeState()
        val mAdapter = MenuAdapter(this, menuList) { _, item ->
            setToolbarTitle(item.name)
            when (item.id) {
                1 -> {
                    replaceFragment(RidFragment())
                }

                2 -> {
                    replaceFragment(DevInfoFragment())
                }

                3 -> {
                    replaceFragment(RangesFragment())
                }

                4 -> {
                    replaceFragment(DeadBandFragment())
                }

                5 -> {
                    replaceFragment(SyncRTFragment())
                }

                6 -> {
                    replaceFragment(CopyFirmwareFragment())
                }

                7 -> {
                    replaceFragment(ClearLogFileFragment())
                }

                8 -> {
                    replaceFragment(SetDefaultLogFragment())
                }

                9 -> {
                    replaceFragment(AddDeleteTagFragment())
                }

                10 -> {
                    replaceFragment(TagListFragment())
                }

                11 -> {
                    replaceFragment(BleFragment())
                }

                12 -> {
                    replaceFragment(BlesigFragment())
                }

                13 -> {
                    replaceFragment(VelocityFragment())
                }

                14 -> {
                    replaceFragment(CameraFragment())
                }
            }
        }
        binding.recyclerviewDetails.adapter = mAdapter
    }
}