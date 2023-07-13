package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sasy.nontag.databinding.FragmentClearLogBinding
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.utils.AppUtils
import com.sasy.nontag.utils.Constants


class ClearLogFileFragment : Fragment() {
    private lateinit var binding: FragmentClearLogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentClearLogBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentClearLogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.buttonClearLog.setOnClickListener {
            (activity as DetailActivity).send("${Constants.CLEAR_LOG}${Constants.CARRIAGE}")
        }
    }
}