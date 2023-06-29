package com.sasy.nontag.ui

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import c.tlgbltcn.library.BluetoothHelperListener
import com.sasy.nontag.R
import com.sasy.nontag.ui.adapter.DeviceHistoryAdapter
import com.sasy.nontag.utils.*
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
        setMenuRecyclerView()
        setonClickListeners()
        setupStatusText()
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
        }
    }

    private fun setonClickListeners() {
        buttonTurnOnBluetooth.setOnClickListener {
            checkBluetoothSupported()
        }
        button_view_details.setOnClickListener {
            launchActivity<DetailActivity> { }
        }
    }

    private fun checkBluetoothSupported() {
        if (isBluetoothAvailable()) {
            checkBluetoothPermissions()
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

    private fun checkBluetoothPermissions() {
        if (hasBluetoothPermission()) {
            if (!bluetoothHelper.isBluetoothEnabled()) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                bleRequestEnable.launch(enableBtIntent)
            }
        } else {
            alert {
                setTitle(getString(R.string.required_permissions))
                setMessage(permissionMissing)
                positiveButton(getString(R.string.accept)) {
                    showToast("requesting permissions")
                    requestPermissionsLauncher.launch(getBluetoothPermissions())
                }
                neutralButton(getString(R.string.cancel)) {
                    showToast("Bailed due to permissions")
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
        } else {
            showToast(permissionMissing)
        }
    }

    private val requestPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (!permissions.entries.all { it.value }) {
                showToast(permissionMissing)
            }
            requestedEnable = false
            checkBluetoothPermissions()
        }


    private fun setMenuRecyclerView() {
        val dummyHistoryList =
            AppUtils.getDummyHistoryList(AppUtils.getArrayListFromJson(this, R.raw.dashboard))
        recyclerview_history.layoutManager = LinearLayoutManager(this)
        recyclerview_history.setHasFixedSize(true)
        val mAdapter = DeviceHistoryAdapter(this, dummyHistoryList) { _, item ->
//            val intent = Intent(this@DashboardActivity, DetailsActivity::class.java)
//            intent.putExtra(AppConstant.SELECTED_DASHBOARD_ITEM_KEY, item.id)
//            startActivity(intent)
//            overridePendingTransition(R.anim.trans_left_in, R.anim.trans_left_out)
        }
        recyclerview_history.adapter = mAdapter
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