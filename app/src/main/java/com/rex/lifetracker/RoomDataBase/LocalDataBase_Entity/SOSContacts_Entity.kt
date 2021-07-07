package com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity

import android.graphics.Bitmap
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(
    tableName = "user_trusted_contacts",
    indices = [Index(value = ["Phone", "Priority"], unique = true)]
)
@Parcelize
data class SOSContacts_Entity(
    @PrimaryKey(autoGenerate = false)
    val Phone: String,
    val Priority: String,
    val Name: String,
    var Image: Bitmap? = null
): Parcelable
