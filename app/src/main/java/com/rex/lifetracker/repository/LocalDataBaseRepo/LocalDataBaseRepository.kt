package com.rex.lifetracker.repository.LocalDataBaseRepo

import androidx.lifecycle.LiveData
import com.rex.lifetracker.RoomDataBase.LocalDataBaseDao
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.DeleteContactsCacheModel

class LocalDataBaseRepository(private val localDataBaseDao: LocalDataBaseDao) {

    val readAllContacts: LiveData<List<SOSContacts_Entity>> = localDataBaseDao.readAllContacts()
    val readAllUserInfo: LiveData<PersonalInfo_Entity> = localDataBaseDao.readAllUserInfo()
    val readAllSIMSlot: LiveData<List<SIM_Entity>> = localDataBaseDao.readAllSIMSlot()
    val readAllCache: LiveData<List<DeleteContactsCacheModel>> = localDataBaseDao.readAllCache()

    suspend fun addContacts(SOSContactsEntity: SOSContacts_Entity) {
        localDataBaseDao.addContacts(SOSContactsEntity)
    }
    suspend fun addUserInfo(personalInfoEntity: PersonalInfo_Entity) {
        localDataBaseDao.addUserInfo(personalInfoEntity)
    }
    suspend fun addSIMSlot(simEntity: SIM_Entity) {
        localDataBaseDao.addSIMSlot(simEntity)
    }
    suspend fun addCache(DeleteContactsCacheModel: DeleteContactsCacheModel) {
        localDataBaseDao.addCache(DeleteContactsCacheModel)
    }

    suspend fun updateContacts(SOSContactsEntity: SOSContacts_Entity) {
        localDataBaseDao.updateContacts(SOSContactsEntity)
    }
    suspend fun updateUserInfo(personalInfoEntity: PersonalInfo_Entity) {
        localDataBaseDao.updateUserInfo(personalInfoEntity)
    }
    suspend fun updateSIMSlot(simEntity: SIM_Entity) {
        localDataBaseDao.updateSIMSlot(simEntity)
    }

    suspend fun deleteContacts(SOSContactsEntity: SOSContacts_Entity){
        localDataBaseDao.deleteContacts(SOSContactsEntity)
    }
    suspend fun deleteUserInfo(personalInfoEntity: PersonalInfo_Entity){
        localDataBaseDao.deleteUserInfo(personalInfoEntity)
    }
    suspend fun deleteSIMSlot(simEntity: SIM_Entity){
        localDataBaseDao.deleteSIMSlot(simEntity)
    }

    suspend fun deleteCache(DeleteContactsCacheModel: DeleteContactsCacheModel){
        localDataBaseDao.deleteCache(DeleteContactsCacheModel)
    }

    suspend fun nukeTable(){
        localDataBaseDao.nukeTableContacts()
      //  localDataBaseDao.nukeTablePersonalInfo()
        localDataBaseDao.nukeTableSim()
        localDataBaseDao.nukeTabledCache()
    }




}