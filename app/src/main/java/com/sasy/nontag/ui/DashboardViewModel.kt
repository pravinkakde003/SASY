package com.sasy.nontag.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DashboardViewModel : ViewModel() {
    val isScanButtonShown = MutableLiveData(false)
}