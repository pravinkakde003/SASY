package com.sasy.nontag.ui

import android.os.Bundle
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
import com.sasy.nontag.utils.replaceFragment

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
        }
        setMenuRecyclerView()
        setupToolbar()
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