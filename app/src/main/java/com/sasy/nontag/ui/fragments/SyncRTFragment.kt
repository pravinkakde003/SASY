package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentSyncRtBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.AppUtils
import com.sasy.nontag.utils.Constants


class SyncRTFragment : Fragment() {
    private lateinit var binding: FragmentSyncRtBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSyncRtBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSyncRtBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.textViewRt.text = AppUtils.getDisplayCurrentDateTime()
        binding.buttonSetRid.setOnClickListener {
            (activity as DetailActivity).send("${Constants.SET_RTC} ${AppUtils.getCurrentDateTime()}${Constants.CARRIAGE}")
        }
    }
}