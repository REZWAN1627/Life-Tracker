package com.rex.lifetracker.model.FireBaseModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class TrustedContactsModel(
    val Image: String = "null",
    val Name: String = "null",
    val Phone: String = "null",
    val Priority: String = "null"
) : Parcelable