package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentDeadbandBinding
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class DeadBandFragment : Fragment() {
    private lateinit var binding: FragmentDeadbandBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var isGetButtonClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDeadbandBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeadbandBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        dashboardViewModel.resetReceivedText()

        binding.buttonSetDb.setOnClickListener {
            val currentValue = binding.buttonIncrementDecrement.getCurrentNumber()
            if (currentValue > 0) {
                if (dashboardViewModel.isConnected()) {
                    (activity as DetailActivity).send("${Constants.SET_XDB} $currentValue${Constants.CARRIAGE}")
                    showDataStatus(
                        DetailActivity.Status.Success
                    )
                } else {
                    showDataStatus(
                        DetailActivity.Status.Error
                    )
                }
            } else {
                showDataStatus(
                    DetailActivity.Status.Error,
                    getString(R.string.please_enter_range_value)
                )
            }
        }

        binding.buttonGetDeadBand.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                isGetButtonClicked = true
                (activity as DetailActivity).send("${Constants.GET_XDB}${Constants.CARRIAGE}")
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error,
                    getString(R.string.not_connected)
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

    private fun observeState() {
        dashboardViewModel.receivedText.observe(
            viewLifecycleOwner
        ) { receivedText ->
            if (isGetButtonClicked) {
                isGetButtonClicked = false
                receivedText?.let {
                    val outputString = receivedText.replace("{0D}{0A}", "")
                    binding.textViewGetDeadBand.text = outputString.trim()
                }
            }
        }
    }
}