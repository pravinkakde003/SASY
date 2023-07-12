package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentRangesBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.showToast


class RangesFragment : Fragment() {
    private lateinit var binding: FragmentRangesBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentRangesBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRangesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSetRange.setOnClickListener {
            val currentValue = binding.buttonIncrementDecrement.getCurrentNumber()
            if (currentValue > 0) {
                (activity as DetailActivity).send(currentValue.toString())
            } else {
                showToast("Please enter range value.")
            }
        }
    }
}