package com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "personal_info",

    indices = [Index(
        value = ["First_Name", "Last_Name",
            "Deactivate_Time",
            "Active_Time",
            "User_Email", ], unique = true
    )]
)
data class PersonalInfo_Entity(

    @PrimaryKey(autoGenerate = false)
    val id: Int,
    @ColumnInfo(name = "First_Name")
    val First_Name: String,
    @ColumnInfo(name = "Last_Name")
    val Last_Name: String,
    @ColumnInfo(name = "Deactivate_Time")
    val Deactivate_Time: String,
    @ColumnInfo(name = "Active_Time")
    val Active_Time: String,
    @ColumnInfo(name = "User_Email")
    val User_Email: String,
    @ColumnInfo(name = "Image")
    var Image: Bitmap? = null

)