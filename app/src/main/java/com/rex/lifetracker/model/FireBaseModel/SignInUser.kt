package com.rex.lifetracker.model.FireBaseModel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class SignInUser(
    var uId: String = "null",
    var name: String = "null",
    var email: String = "null",
    var imageUrl: String = "null",
    var isAuth: Boolean = false
) : Parcelable

