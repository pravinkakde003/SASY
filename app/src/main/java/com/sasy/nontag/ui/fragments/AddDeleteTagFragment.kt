package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentAddDelTagBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.hideKeyBoard
import com.sasy.nontag.utils.showToast


class AddDeleteTagFragment : Fragment() {
    private lateinit var binding: FragmentAddDelTagBinding

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
                    requireActivity().hideKeyBoard()
                    (activity as DetailActivity).send("${Constants.ADD_TAG} $currentValue${Constants.CARRIAGE}")
                } else {
                    showToast(getString(R.string.enter_tag_value))
                }
            }
        }

        binding.buttonDeleteTagID.setOnClickListener {
            val currentValue = binding.editTextTagID.text
            currentValue?.let {
                if (currentValue.isNotEmpty()) {
                    requireActivity().hideKeyBoard()
                    (activity as DetailActivity).send("${Constants.DELETE_TAG} $currentValue${Constants.CARRIAGE}")
                } else {
                    showToast(getString(R.string.enter_tag_value))
                }
            }
        }
    }
}