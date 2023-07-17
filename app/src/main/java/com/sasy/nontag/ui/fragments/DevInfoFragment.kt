package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentDevInfoBinding
import com.sasy.nontag.model.DevInfoModel
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.ui.adapter.DevInfoAdapter
import com.sasy.nontag.utils.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class DevInfoFragment : Fragment() {
    private lateinit var binding: FragmentDevInfoBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDevInfoBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDevInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        dashboardViewModel.resetReceivedText()
        binding.buttonGetDevInfo.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                (activity as DetailActivity).send("${Constants.DEVICE_INFO} ${Constants.CARRIAGE}")
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error
                )
            }
        }
    }

    private fun observeState() {
        dashboardViewModel.receivedText.observe(
            this
        ) { receivedText ->
            receivedText?.let {
                setDataRecyclerView(receivedText.trim())
            }
        }
    }

    private fun setDataRecyclerView(inputString: String) {
        val yourArray: List<String> = inputString.split("{0A}", "{0D}")
        val filteredArray = yourArray.filter { !it.isNullOrBlank() }
        val mArrayList = arrayListOf<DevInfoModel>()
        for (item in filteredArray) {
            item.split(Regex("->")).let {
                mArrayList.add(DevInfoModel(it[0], it.getOrNull(1) ?: ""))
            }
        }
        val layoutManager = GridLayoutManager(requireActivity(), 4)
        binding.devInfoRecyclerView.layoutManager = layoutManager

        binding.devInfoRecyclerView.setHasFixedSize(true)
        val mAdapter = DevInfoAdapter(requireActivity(), mArrayList)
        binding.devInfoRecyclerView.adapter = mAdapter
        val linearSmoothScroller: LinearSmoothScroller =
            object : LinearSmoothScroller(requireContext()) {
                override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                    return 1000f / displayMetrics.densityDpi
                }
            }
        linearSmoothScroller.targetPosition = mAdapter.itemCount - 1
        layoutManager.startSmoothScroll(linearSmoothScroller)

        GlobalScope.launch(Dispatchers.Main) {
            delay(1500)
            binding.devInfoRecyclerView.smoothScrollToPosition(0)
        }
    }

    private fun showDataStatus(status: DetailActivity.Status, statusMsg: String = "") {
        binding.dataSentStatusTextView.visibility = View.VISIBLE
        if (status == DetailActivity.Status.Success) {
            binding.dataSentStatusTextView.setBackgroundResource(R.color.transparent_green)
            binding.dataSentStatusTextView.text = getString(R.string.sent)
        } else if (status == DetailActivity.Status.Error) {
            binding.dataSentStatusTextView.setBackgroundResource(R.color.transparent_red)
            if (statusMsg.isNullOrEmpty()) {
                binding.dataSentStatusTextView.text = getString(R.string.not_sent)
            } else {
                binding.dataSentStatusTextView.text = statusMsg
            }
        }

        binding.dataSentStatusTextView.postDelayed({
            binding.dataSentStatusTextView.visibility = View.GONE
        }, Constants.TOAST_DELAY)
    }
}