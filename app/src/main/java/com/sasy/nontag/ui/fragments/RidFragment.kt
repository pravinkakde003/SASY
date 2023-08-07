package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentRidBinding
import com.sasy.nontag.ui.viewmodel.DashboardViewModel
import com.sasy.nontag.ui.activity.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.hideKeyBoard


class RidFragment : Fragment() {
    private lateinit var binding: FragmentRidBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var isGetButtonClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRidBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRidBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        dashboardViewModel.resetReceivedText()
        binding.buttonSetRid.setOnClickListener {
            val currentValue = binding.editTextRid.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    if (currentValue.length >= 10) {
                        if (dashboardViewModel.isConnected()) {
                            requireActivity().hideKeyBoard()
                            (activity as DetailActivity).send("${Constants.SET_RID} $currentValue${Constants.CARRIAGE}")
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
                            getString(R.string.please_enter_rid_value)
                        )
                    }
                } else {
                    showDataStatus(
                        DetailActivity.Status.Error,
                        getString(R.string.rid_value_length_error)
                    )
                }
            }
        }

        binding.buttonGetRid.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                isGetButtonClicked = true
                (activity as DetailActivity).send("${Constants.GET_RID}${Constants.CARRIAGE}")
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

    private fun observeState() {
        dashboardViewModel.receivedText.observe(
            viewLifecycleOwner
        ) { receivedText ->
            if (isGetButtonClicked) {
                isGetButtonClicked = false
                receivedText?.let {
                    val outputString = receivedText.replace("{0D}{0A}", "")
                    binding.textViewRid.text = outputString.trim()
                }
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