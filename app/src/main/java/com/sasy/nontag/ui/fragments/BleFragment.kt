package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentBleBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class BleFragment : Fragment() {
    private lateinit var binding: FragmentBleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBleBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTurnOnBle.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_MODE_ON_OFF} 1${Constants.CARRIAGE}")
        }
        binding.buttonTurnOffBle.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_MODE_ON_OFF} 0${Constants.CARRIAGE}")
        }
    }
}