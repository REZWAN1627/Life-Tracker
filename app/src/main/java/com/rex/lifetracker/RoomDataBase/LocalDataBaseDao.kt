package com.rex.lifetracker.RoomDataBase

import androidx.lifecycle.LiveData
import androidx.room.*
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.deleteContactsCacheModel

@Dao
interface LocalDataBaseDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addContacts(SOSContactsEntity: SOSContacts_Entity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserInfo(personalInfoEntity: PersonalInfo_Entity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSIMSlot(simEntity: SIM_Entity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addCache(deleteContactsCacheModel: deleteContactsCacheModel)

    @Update
    suspend fun updateContacts(SOSContactsEntity: SOSContacts_Entity)

    @Update
    suspend fun updateUserInfo(personalInfoEntity: PersonalInfo_Entity)

    @Update
    suspend fun updateSIMSlot(simEntity: SIM_Entity)

    @Delete
    suspend fun deleteContacts(SOSContactsEntity: SOSContacts_Entity)

    @Delete
    suspend fun deleteUserInfo(personalInfoEntity: PersonalInfo_Entity)

    @Delete
    suspend fun deleteSIMSlot(simEntity: SIM_Entity)

    @Delete
    suspend fun deleteCache(deleteContactsCacheModel: deleteContactsCacheModel)

    @Query("SELECT * FROM user_trusted_contacts")
    fun readAllContacts(): LiveData<List<SOSContacts_Entity>>

    @Query("SELECT * FROM personal_info")
    fun readAllUserInfo(): LiveData<List<PersonalInfo_Entity>>

    @Query("SELECT * FROM sim_table")
    fun readAllSIMSlot(): LiveData<List<SIM_Entity>>

    @Query("SELECT * FROM deleteCache")
    fun readAllCache(): LiveData<List<deleteContactsCacheModel>>



    @Query("DELETE FROM user_trusted_contacts")
   suspend fun nukeTableContacts()

    @Query("DELETE FROM personal_info")
   suspend fun nukeTablePersonalInfo()

    @Query("DELETE FROM sim_table")
  suspend  fun nukeTableSim()

    @Query("DELETE FROM deleteCache")
   suspend fun nukeTabledCache()
}