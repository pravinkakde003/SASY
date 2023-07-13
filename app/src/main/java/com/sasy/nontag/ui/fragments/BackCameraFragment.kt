package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentBackCameraBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.Constants


class BackCameraFragment : Fragment() {
    private lateinit var binding: FragmentBackCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentBackCameraBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBackCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonTurnOn.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_BACK_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
        }
        binding.buttonTurnOff.setOnClickListener {
            (activity as DetailActivity).send("${Constants.BLE_BACK_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
        }
    }
}