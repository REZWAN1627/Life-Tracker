package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.model.FireBaseModel.SignInUser
import com.rex.lifetracker.utils.Constant.TAG


class SignInRepository {
    private val firebaseAuth = Firebase.auth
    private val user = SignInUser()

    private var check = true

    // check Authentication in firebase..........

    fun checkAuthenticationInFirebase(): MutableLiveData<SignInUser>? {
        var isAuthenticateLiveData = MutableLiveData<SignInUser>()
        val currentUser = firebaseAuth.currentUser
        Log.d(TAG, "checkAuthenticationInFirebase: current user ${currentUser?.email}")
        if (currentUser == null) {
            val user = SignInUser("null","null","null","null",false)
            Log.d(TAG, "checkAuthenticationInFirebase: in repository")
//            user.isAuth = false
           check = false
            isAuthenticateLiveData.value = user
        } else {
            Log.d(TAG, "checkAuthenticationInFirebase: is called when user not null")
            val user = SignInUser("null","null","null","null",true)
//            user.uId = currentUser.uid
//            user.isAuth = true
           check = true
            isAuthenticateLiveData.value = user
        }
        return isAuthenticateLiveData
    }

    //collect user info  from authentication..........

    fun collectUserData(): MutableLiveData<SignInUser>? {
        val collectUserMutableLiveData = MutableLiveData<SignInUser>()
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            Log.d(TAG, "collectUserData: is called")
            val uId = currentUser.uid
            val name = currentUser.displayName
            val email = currentUser.email
            val getImageUrl = currentUser.photoUrl
            val imageUrl = getImageUrl.toString()
            val user = SignInUser(uId, name.toString(), email.toString(), imageUrl.toString(),true)
            collectUserMutableLiveData.value = user
        }
        return collectUserMutableLiveData
    }


    //firebase sign in with google

    //firebase sign in with google
    fun firebaseSignInWithGoogle(authCredential: AuthCredential?): MutableLiveData<String>? {
        val authMutableLiveData = MutableLiveData<String>()
        firebaseAuth.signInWithCredential(authCredential!!).addOnSuccessListener {
            val currentUser = firebaseAuth.currentUser
            val uId = currentUser?.uid
            authMutableLiveData.value = uId!!

        }.addOnFailureListener { e -> authMutableLiveData.value = e.toString() }
        return authMutableLiveData
    }


}