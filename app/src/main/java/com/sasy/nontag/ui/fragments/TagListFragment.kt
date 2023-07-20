package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentTagListBinding
import com.sasy.nontag.model.DevInfoModel
import com.sasy.nontag.ui.DashboardViewModel
import com.sasy.nontag.ui.DetailActivity
import com.sasy.nontag.ui.adapter.TagListAdapter
import com.sasy.nontag.utils.Constants
import com.sasy.nontag.utils.hide
import com.sasy.nontag.utils.show


class TagListFragment : Fragment() {
    private lateinit var binding: FragmentTagListBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private var isGetButtonClicked: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentTagListBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTagListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeState()
        dashboardViewModel.resetReceivedText()
        setInitialView()
        binding.buttonGetTagList.setOnClickListener {
            if (dashboardViewModel.isConnected()) {
                isGetButtonClicked = true
                (activity as DetailActivity).send("${Constants.GET_TAG_LIST}${Constants.CARRIAGE}")
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error
                )
            }
        }
    }

    private fun observeState() {
        dashboardViewModel.receivedText.observe(
            viewLifecycleOwner
        ) { receivedText ->
            if (isGetButtonClicked) {
                setViewAfterReceivedData()
                isGetButtonClicked = false
                receivedText?.let {
                    setDataRecyclerView(receivedText.trim())
                }
            }
        }
    }

    private fun setViewAfterReceivedData() {
        binding.textViewTitle.hide()
        binding.textViewToolTip.hide()
        binding.recyclerViewTagList.show()
    }

    private fun setInitialView() {
        binding.textViewTitle.show()
        binding.textViewToolTip.show()
        binding.recyclerViewTagList.hide()
    }


    private fun setDataRecyclerView(inputString: String) {
        val yourArray: List<String> = inputString.split("{0A}", "{0D}")
        val filteredArray = yourArray.filter { !it.isNullOrBlank() }
        val mArrayList = arrayListOf<DevInfoModel>()
        for (item in filteredArray) {
            item.split(Regex("->")).let {
                mArrayList.add(DevInfoModel(it[0], it.getOrNull(1) ?: ""))
            }
        }
        val layoutManager = GridLayoutManager(requireActivity(), 4)
        binding.recyclerViewTagList.layoutManager = layoutManager

        binding.recyclerViewTagList.setHasFixedSize(true)
        val mAdapter = TagListAdapter(requireActivity(), mArrayList)
        binding.recyclerViewTagList.adapter = mAdapter
    }

    private fun showDataStatus(status: DetailActivity.Status, statusMsg: String = "") {
        binding.dataSentStatusTextView.visibility = View.VISIBLE
        if (status == DetailActivity.Status.Success) {
            binding.dataSentStatusTextView.setBackgroundResource(R.color.transparent_green)
            binding.dataSentStatusTextView.text = getString(R.string.sent)
        } else if (status == DetailActivity.Status.Error) {
            binding.dataSentStatusTextView.setBackgroundResource(R.color.transparent_red)
            if (statusMsg.isNullOrEmpty()) {
                binding.dataSentStatusTextView.text = getString(R.string.not_sent)
            } else {
                binding.dataSentStatusTextView.text = statusMsg
            }
        }

        binding.dataSentStatusTextView.postDelayed({
            binding.dataSentStatusTextView.visibility = View.GONE
        }, Constants.TOAST_DELAY)
    }
}