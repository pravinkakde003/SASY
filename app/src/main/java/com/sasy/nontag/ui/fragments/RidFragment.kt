package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentRidBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.hideKeyBoard
import com.sasy.nontag.utils.showToast


class RidFragment : Fragment() {
    private lateinit var binding: FragmentRidBinding

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
        binding.buttonSetRid.setOnClickListener {
            val currentValue = binding.editTextRid.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    if (currentValue.length >= 10) {
                        requireActivity().hideKeyBoard()
                        (activity as DetailActivity).send("${Constants.SET_RID} $currentValue${Constants.CARRIAGE}")
                    } else {
                        showToast(getString(R.string.please_enter_rid_value))
                    }
                } else {
                    showToast(getString(R.string.rid_value_length_error))
                }
            }
        }
    }
}