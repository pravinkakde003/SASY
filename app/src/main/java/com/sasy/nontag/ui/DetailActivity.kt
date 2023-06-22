package com.sasy.nontag.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.ui.adapter.MenuAdapter
import com.sasy.nontag.ui.fragments.BlesigFragment
import com.sasy.nontag.ui.fragments.RangesFragment
import com.sasy.nontag.utils.AppUtils
import com.sasy.nontag.utils.replaceFragment
import kotlinx.android.synthetic.main.activity_details.*
import kotlinx.android.synthetic.main.toolbar_layout.view.*

class DetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_details)
        setMenuRecyclerView()
        setupToolbar()
    }

    private fun setupToolbar() {
        toolbar.backArrowImage.setOnClickListener {
            onBackPressed()
        }
    }

    private fun setToolbarTitle(toolbarTitle: String) {
        toolbar.txtDashboardTitle.text = toolbarTitle
    }

    private fun setMenuRecyclerView() {
        val menuList =
            AppUtils.getMenuList(AppUtils.getArrayListFromJson(this, R.raw.menu_item))
        recyclerviewDetails.layoutManager = LinearLayoutManager(this)
        recyclerviewDetails.setHasFixedSize(true)
        setToolbarTitle(resources.getString(R.string.ranges))
        replaceFragment(BlesigFragment())
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
        recyclerviewDetails.adapter = mAdapter
    }
}