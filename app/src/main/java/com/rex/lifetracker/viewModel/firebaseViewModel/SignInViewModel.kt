package com.rex.lifetracker.viewModel.firebaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.google.firebase.auth.AuthCredential
import com.rex.lifetracker.model.FireBaseModel.SignInUser
import com.rex.lifetracker.repository.firebaseRepository.SignInRepository


class SignInViewModel(application: Application) : AndroidViewModel(application) {
    private var  signInRepository : SignInRepository = SignInRepository()

    var checkAuthenticateLiveData: LiveData<SignInUser>? = null
    var collectUserInfoLiveData: LiveData<SignInUser> = signInRepository.collectUserData()
    var authenticateUserLiveData: LiveData<String>? = null

    //firebase sign in with google
    fun signInWithGoogle(authCredential: AuthCredential?) {
        authenticateUserLiveData = signInRepository.firebaseSignInWithGoogle(authCredential)
    }

    fun checkAuth(){
        checkAuthenticateLiveData = signInRepository.checkAuthenticationInFirebase()
    }



}