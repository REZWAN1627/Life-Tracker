package com.rex.lifetracker.viewModel.firebaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rex.lifetracker.model.FireBaseModel.AppsInformationModel
import com.rex.lifetracker.repository.firebaseRepository.AppsInformationRepository

class AppsInformationViewModel(application: Application) : AndroidViewModel(application) {

  private  val appsInformationRepository: AppsInformationRepository = AppsInformationRepository()
    var getAppsInformationLiveData: MutableLiveData<AppsInformationModel?> =
        MutableLiveData<AppsInformationModel?>()

    fun getAppsInformation() {
        getAppsInformationLiveData = appsInformationRepository.getAppsInformation()
    }
}