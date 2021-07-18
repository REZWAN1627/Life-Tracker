package com.rex.lifetracker.viewModel.firebaseViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.model.FireBaseModel.TrustedContactsModel
import com.rex.lifetracker.repository.firebaseRepository.TrustedContactsRepository

class TrustedContactsViewModel : ViewModel() {

    private val trustedContactsRepository = TrustedContactsRepository()

    var getContactsLiveData =
        trustedContactsRepository.getTrustedContactsInfo()


    var getInsertedLiveData: MutableLiveData<String>? =null


    fun insertTrustedContactsInfo(trustedContactsModel: TrustedContactsModel) {
       getInsertedLiveData = trustedContactsRepository.insertTrustedContactsInfo(trustedContactsModel)
    }

    fun deleteContact(deleteContact: String){
        trustedContactsRepository.deleteTrustedContacts(deleteContact)!!


    }

    fun updateDataTOFireBase(model: SOSContacts_Entity, image: ByteArray?){
        trustedContactsRepository.updateDataTOFireBase(model,image)
    }


}