package com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sim_table")
data class SIM_Entity(
    @PrimaryKey(autoGenerate = false)
    val id:Int,
    val SELECTED_SIM_SLOT:String
)
