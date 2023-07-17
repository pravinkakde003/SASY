package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentRightCameraBinding
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class RightCameraFragment : Fragment() {
    private lateinit var binding: FragmentRightCameraBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRightCameraBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRightCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTurnOn.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                (activity as DetailActivity).send("${Constants.BLE_RIGHT_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error
                )
            }
        }
        binding.buttonTurnOff.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                (activity as DetailActivity).send("${Constants.BLE_RIGHT_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
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