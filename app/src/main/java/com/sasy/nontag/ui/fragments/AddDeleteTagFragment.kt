package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentAddDelTagBinding
import com.sasy.nontag.ui.viewmodel.DashboardViewModel
import com.sasy.nontag.ui.activity.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.Constants.TOAST_DELAY
import com.sasy.nontag.utils.hideKeyBoard


class AddDeleteTagFragment : Fragment() {
    private lateinit var binding: FragmentAddDelTagBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentAddDelTagBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDelTagBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonAddTagID.setOnClickListener {
            val currentValue = binding.editTextTagID.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    if (dashboardViewModel.isConnected()) {
                        requireActivity().hideKeyBoard()
                        (activity as DetailActivity).send("${Constants.ADD_TAG} $currentValue${Constants.CARRIAGE}")
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
                        getString(R.string.enter_tag_value)
                    )
                }
            }
        }

        binding.buttonDeleteTagID.setOnClickListener {
            val currentValue = binding.editTextTagID.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    if (dashboardViewModel.isConnected()) {
                        requireActivity().hideKeyBoard()
                        (activity as DetailActivity).send("${Constants.DELETE_TAG} $currentValue${Constants.CARRIAGE}")
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
                        getString(R.string.enter_tag_value)
                    )
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
        }, TOAST_DELAY)
    }
}