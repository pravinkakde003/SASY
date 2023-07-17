package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentDatabaseBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.showToast


class DataBaseFragment : Fragment() {
    private lateinit var binding: FragmentDatabaseBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentDatabaseBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDatabaseBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSetDb.setOnClickListener {
            val currentValue = binding.buttonIncrementDecrement.getCurrentNumber()
            if (currentValue > 0) {
                (activity as DetailActivity).send("${Constants.SET_DB} $currentValue${Constants.CARRIAGE}")
            } else {
                showToast(getString(R.string.please_enter_range_value))
            }
        }
    }
}