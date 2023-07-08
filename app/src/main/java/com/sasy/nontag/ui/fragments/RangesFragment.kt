package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.sasy.nontag.R
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.utils.showToast


class RangesFragment : Fragment() {
    private val dashboardViewModel: DashboardViewModel by viewModels()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_ranges, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        showToast(dashboardViewModel.selectedDeviceName.value + " " + dashboardViewModel.selectedDeviceAddress.value)
    }
}