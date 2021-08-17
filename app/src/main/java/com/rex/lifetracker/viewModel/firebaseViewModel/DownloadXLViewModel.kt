package com.rex.lifetracker.viewModel.firebaseViewModel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.rex.lifetracker.repository.firebaseRepository.DownloadXLRepository

class DownloadXLViewModel:ViewModel() {
    private val repository = DownloadXLRepository()

    fun downloadXLSheet(context: Context){
        repository.downloadXLSheet(context)
    }
}