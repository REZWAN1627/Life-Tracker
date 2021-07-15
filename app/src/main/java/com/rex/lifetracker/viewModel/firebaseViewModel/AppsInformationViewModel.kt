package com.rex.lifetracker.viewModel.firebaseViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.AppsAdminModel
import com.rex.lifetracker.repository.firebaseRepository.AppsInformationRepository

class AppsInformationViewModel : ViewModel() {

    private val appsInformationRepository: AppsInformationRepository = AppsInformationRepository()

    val getAppsInformationLiveData: MutableLiveData<AppsAdminModel?> =
        appsInformationRepository.getAppsInformation()


}