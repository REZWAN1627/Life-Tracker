package com.rex.lifetracker.viewModel.firebaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.rex.lifetracker.model.FireBaseModel.SosModel
import com.rex.lifetracker.repository.firebaseRepository.SosRepository

class SosViewModel(application: Application) : AndroidViewModel(application) {

    private val sosRepository: SosRepository = SosRepository()
    var insertResultLiveData: LiveData<String>? = null
    var getSOSContactsLiveData: LiveData<List<SosModel>>? = null

    fun insert(userinfo: SosModel) {
        insertResultLiveData = sosRepository.insertSOSContactsFirebase(userinfo)
    }

    fun getSOSContacts() {
        getSOSContactsLiveData = sosRepository.getSOSContactsFromDataBase()
    }

}