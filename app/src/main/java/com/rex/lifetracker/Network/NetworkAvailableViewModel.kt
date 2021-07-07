package com.rex.lifetracker.Network

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class NetworkAvailableViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = NetworkAvailable()

    val isNetWorkAvailable: MutableLiveData<Boolean>? = repo.isNetworkAvailable(application)
}