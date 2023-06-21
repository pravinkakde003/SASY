package com.sasy.nontag.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.ui.adapter.DeviceHistoryAdapter
import com.sasy.nontag.utils.AppUtils
import kotlinx.android.synthetic.main.activity_dashboard.*


class DashboardActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        pulseLayout.startPulse()
        setMenuRecyclerView()

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
}