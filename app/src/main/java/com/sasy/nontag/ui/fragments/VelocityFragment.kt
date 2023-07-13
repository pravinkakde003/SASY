package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentVelocityBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.hideKeyBoard
import com.sasy.nontag.utils.showToast


class VelocityFragment : Fragment() {
    private lateinit var binding: FragmentVelocityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentVelocityBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentVelocityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonSetVelocity.setOnClickListener {
            val currentValue = binding.editTextVelocity.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    requireActivity().hideKeyBoard()
                    (activity as DetailActivity).send("${Constants.SET_VELOCITY} $currentValue${Constants.CARRIAGE}")
                } else {
                    showToast(getString(R.string.please_enter_velocity))
                }
            }
        }
    }
}