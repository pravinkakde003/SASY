package com.sasy.nontag.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.sasy.nontag.R
import com.sasy.nontag.databinding.FragmentCameraBinding
import com.sasy.nontag.ui.viewmodel.DashboardViewModel
import com.sasy.nontag.ui.activity.DetailActivity
import com.sasy.nontag.utils.Constants


class CameraFragment : Fragment() {
    private lateinit var binding: FragmentCameraBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
    private lateinit var checkedID: CameraConstant

    enum class CameraConstant {
        FRONT_CAMERA, BACK_CAMERA, LEFT_CAMERA, RIGHT_CAMERA
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentCameraBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnCheckedChangeListener()

        binding.buttonTurnOn.setOnClickListener {
            setCameraTurnOnCommand()
        }

        binding.buttonTurnOff.setOnClickListener {
            setCameraTurnOffCommand()
        }
    }

    private fun setCameraTurnOffCommand() {
        if (::checkedID.isInitialized) {
            if (dashboardViewModel.isConnected()) {
                when (checkedID) {
                    CameraConstant.FRONT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_FRONT_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
                    }

                    CameraConstant.BACK_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_BACK_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
                    }

                    CameraConstant.LEFT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_LEFT_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
                    }

                    CameraConstant.RIGHT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_RIGHT_CAMERA_ENABLE_DISABLE} 0${Constants.CARRIAGE}")
                    }
                }
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error
                )
            }
        } else {
            showDataStatus(
                DetailActivity.Status.Error,
                getString(R.string.please_select_camera_value)
            )
        }
    }

    private fun setCameraTurnOnCommand() {
        if (::checkedID.isInitialized) {
            if (dashboardViewModel.isConnected()) {
                when (checkedID) {
                    CameraConstant.FRONT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_FRONT_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
                    }

                    CameraConstant.BACK_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_BACK_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
                    }

                    CameraConstant.LEFT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_LEFT_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
                    }

                    CameraConstant.RIGHT_CAMERA -> {
                        (activity as DetailActivity).send("${Constants.BLE_RIGHT_CAMERA_ENABLE_DISABLE} 1${Constants.CARRIAGE}")
                    }
                }
                showDataStatus(
                    DetailActivity.Status.Success
                )
            } else {
                showDataStatus(
                    DetailActivity.Status.Error
                )
            }
        } else {
            showDataStatus(
                DetailActivity.Status.Error,
                getString(R.string.please_select_camera_value)
            )
        }
    }

    private fun setOnCheckedChangeListener() {
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when {
                R.id.radioButtonFrontCamera == checkedId -> {
                    checkedID = CameraConstant.FRONT_CAMERA
                }

                R.id.radioButtonBackCamera == checkedId -> {
                    checkedID = CameraConstant.BACK_CAMERA
                }

                R.id.radioButtonLeftCamera == checkedId -> {
                    checkedID = CameraConstant.LEFT_CAMERA
                }

                R.id.radioButtonRightCamera == checkedId -> {
                    checkedID = CameraConstant.RIGHT_CAMERA
                }
            }
        }
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