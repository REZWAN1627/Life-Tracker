package com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "deleteCache", indices = [Index(value = ["deleteNumber"],unique = true)])
data class DeleteContactsCacheModel(

    @PrimaryKey(autoGenerate = false)
    val deleteNumber:String

)
