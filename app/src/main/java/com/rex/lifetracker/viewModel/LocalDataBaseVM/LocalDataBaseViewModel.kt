package com.rex.lifetracker.viewModel.LocalDataBaseVM

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.rex.lifetracker.RoomDataBase.LocalDataBase
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.deleteContactsCacheModel
import com.rex.lifetracker.repository.LocalDataBaseRepo.LocalDataBaseRepository
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocalDataBaseViewModel(application: Application) : AndroidViewModel(application) {
    val readAllContacts: LiveData<List<SOSContacts_Entity>>
    val realAllUserInfo: LiveData<PersonalInfo_Entity>
    val readAllSIMSlot: LiveData<List<SIM_Entity>>
    val readAllCache: LiveData<List<deleteContactsCacheModel>>
    private val databaseRepository: LocalDataBaseRepository

    init {
        val userContactsDao = LocalDataBase.getDatabase(application).userContactsDao()
        databaseRepository = LocalDataBaseRepository(userContactsDao)
        readAllContacts = databaseRepository.readAllContacts
        realAllUserInfo = databaseRepository.readAllUserInfo
        readAllSIMSlot = databaseRepository.readAllSIMSlot
        readAllCache = databaseRepository.readAllCache
    }

    fun addContacts(SOSContactsEntity: SOSContacts_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.addContacts(SOSContactsEntity)
        }
    }

    fun addUserInfo(personalInfoEntity: PersonalInfo_Entity) {
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.addUserInfo(personalInfoEntity)
        }
    }

    fun addSIMSlot(simEntity: SIM_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.addSIMSlot(simEntity)
        }
    }

    fun addCache(deleteContactsCacheModel: deleteContactsCacheModel) {
        Log.d(TAG, "Cache")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.addCache(deleteContactsCacheModel)
        }
    }

    fun updateContacts(SOSContactsEntity: SOSContacts_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.updateContacts(SOSContactsEntity)
        }
    }

    fun updateUserInfo(personalInfoEntity: PersonalInfo_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.updateUserInfo(personalInfoEntity)
        }
    }

    fun updateSIMSlot(simEntity: SIM_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.updateSIMSlot(simEntity)
        }
    }

    fun deleteContacts(SOSContactsEntity: SOSContacts_Entity) {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.deleteContacts(SOSContactsEntity)
        }
    }

    fun deleteUserInfo(personalInfoEntity: PersonalInfo_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.deleteUserInfo(personalInfoEntity)
        }
    }

    fun deleteSIMSlot(simEntity: SIM_Entity) {
        Log.d(TAG, "addContacts: is called")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.deleteSIMSlot(simEntity)
        }
    }

    fun deleteCache(deleteContactsCacheModel: deleteContactsCacheModel) {
        Log.d(TAG, "Cache")
        viewModelScope.launch(Dispatchers.IO) {

            databaseRepository.deleteCache(deleteContactsCacheModel)
        }
    }

    fun nukeTable() {
        viewModelScope.launch(Dispatchers.IO) {
            databaseRepository.nukeTable()
        }
    }
}