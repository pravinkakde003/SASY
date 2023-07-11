package com.sasy.nontag.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.databinding.ActivityDetailsBinding
import com.sasy.nontag.ui.adapter.MenuAdapter
import com.sasy.nontag.ui.fragments.BlesigFragment
import com.sasy.nontag.ui.fragments.RangesFragment
import com.sasy.nontag.utils.AppUtils
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.bluetooth_utils.BluetoothState
import com.sasy.nontag.utils.replaceFragment
import com.sasy.nontag.utils.setTextColorRes

class DetailActivity : AppCompatActivity() {
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
        }
        setMenuRecyclerView()
        setupToolbar()
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.setBluetoothState(BluetoothState.ConnectingState)
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

    private fun setToolbarTitle(toolbarTitle: String) {
        binding.toolbar.txtDashboardTitle.text = toolbarTitle
    }

    private fun setMenuRecyclerView() {
        val menuList =
            AppUtils.getMenuList(AppUtils.getArrayListFromJson(this, R.raw.menu_item))
        binding.recyclerviewDetails.layoutManager = LinearLayoutManager(this)
        binding.recyclerviewDetails.setHasFixedSize(true)
        setToolbarTitle(resources.getString(R.string.ranges))
        replaceFragment(RangesFragment())
        observeState()
        val mAdapter = MenuAdapter(this, menuList) { _, item ->
            setToolbarTitle(item.name)
            when (item.id) {
                1 -> {
                    replaceFragment(RangesFragment())
                }

                6 -> {
                    replaceFragment(BlesigFragment())
                }
            }
        }
        binding.recyclerviewDetails.adapter = mAdapter
    }
}