package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentBlesigBinding
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.seekbar.BubbleSeekBar


class BlesigFragment : Fragment() {
    private lateinit var binding: FragmentBlesigBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    var selectedBlesigValue = ""
    private var isGetButtonClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBlesigBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBlesigBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun observeState() {
        dashboardViewModel.receivedText.observe(
            viewLifecycleOwner
        ) { receivedText ->
            if (isGetButtonClicked) {
                isGetButtonClicked = false
                receivedText?.let {
                    val outputString = receivedText.replace("{0D}{0A}", "")
                    binding.textViewGetBlesig.text = outputString.trim()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        binding.seekBar.setCustomSectionTextArray { _, array ->
            getBlesigArray(array)
            array
        }
        binding.seekBar.setProgress(5f)
        selectedBlesigValue = "A8"
        binding.seekBar.onProgressChangedListener =
            object : BubbleSeekBar.OnProgressChangedListenerAdapter() {
                override fun onProgressChanged(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    binding.seekBar.setBubbleProgressText(getBlesigArrayList()[progress - 1])
                }

                override fun getProgressOnActionUp(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float
                ) {
                }

                override fun getProgressOnFinally(
                    bubbleSeekBar: BubbleSeekBar,
                    progress: Int,
                    progressFloat: Float,
                    fromUser: Boolean
                ) {
                    selectedBlesigValue = getBlesigArrayList()[progress - 1]
                }
            }

        binding.buttonSetBlesig.setOnClickListener {
            if (selectedBlesigValue.isNotEmpty()) {
                if (dashboardViewModel.isConnected()) {
                    val finalBlesigValue = getEquivalentBlesigValue(selectedBlesigValue)
                    (activity as DetailActivity).send("${Constants.SET_BLESIG} $finalBlesigValue${Constants.CARRIAGE}")
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
                    getString(R.string.please_enter_blesig_value)
                )
            }
        }

        binding.buttonGetBlesig.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                isGetButtonClicked = true
                (activity as DetailActivity).send("${Constants.GET_BLESIG} ${Constants.CARRIAGE}")
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

    private fun getBlesigArray(array: SparseArray<String>) {
        array.clear()
        array.put(0, "AC")
        array.put(1, "AB")
        array.put(2, "AA")
        array.put(3, "A9")
        array.put(4, "A8")
        array.put(5, "A7")
        array.put(6, "A6")
        array.put(7, "A5")
        array.put(8, "A4")
    }

    private fun getBlesigArrayList(): ArrayList<String> {
        val array = arrayListOf<String>()
        array.clear()
        array.add(0, "AC")
        array.add(1, "AB")
        array.add(2, "AA")
        array.add(3, "A9")
        array.add(4, "A8")
        array.add(5, "A7")
        array.add(6, "A6")
        array.add(7, "A5")
        array.add(8, "A4")
        return array
    }

    fun getEquivalentBlesigValue(selectedValue: String): String {
        when (selectedValue) {
            "AC" -> {
                return "-84"
            }

            "AB" -> {
                return "-85"
            }

            "AA" -> {
                return "-86"
            }

            "A9" -> {
                return "-87"
            }

            "A8" -> {
                return "-88"
            }

            "A7" -> {
                return "-89"
            }

            "A6" -> {
                return "-90"
            }

            "A5" -> {
                return "-91"
            }

            "A4" -> {
                return "-92"
            }
        }
        return ""
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