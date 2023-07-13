package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentCopyFirmwareBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class CopyFirmwareFragment : Fragment() {
    private lateinit var binding: FragmentCopyFirmwareBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCopyFirmwareBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCopyFirmwareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonCopyFirmware.setOnClickListener {
            (activity as DetailActivity).send("${Constants.COPY_FIRMWARE}${Constants.CARRIAGE}")
        }
    }
}