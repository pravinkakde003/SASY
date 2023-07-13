package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentBleBinding
import com.sasy.nontag.databinding.FragmentFrontCameraBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class FrontCameraFragment : Fragment() {
    private lateinit var binding: FragmentFrontCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentFrontCameraBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFrontCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTurnOn.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_FRONT_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
        }
        binding.buttonTurnOff.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_FRONT_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
        }
    }
}