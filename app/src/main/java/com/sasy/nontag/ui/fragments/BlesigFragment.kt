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
import com.sasy.nontag.utils.showToast


class BlesigFragment : Fragment() {
    private lateinit var binding: FragmentBlesigBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    var selectedBlesigValue = ""

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
            this
        ) { receivedText ->
            receivedText?.let {
                binding.textViewGetBlesig.text = receivedText.trim()
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
        selectedBlesigValue = "AB"
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
                (activity as DetailActivity).send("${Constants.SET_BLESIG} $selectedBlesigValue${Constants.CARRIAGE}")
            } else {
                showToast(getString(R.string.please_enter_blesig_value))
            }
        }

        binding.buttonGetBlesig.setOnClickListener {
            (activity as DetailActivity).send("${Constants.GET_BLESIG} ${Constants.CARRIAGE}")
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
}