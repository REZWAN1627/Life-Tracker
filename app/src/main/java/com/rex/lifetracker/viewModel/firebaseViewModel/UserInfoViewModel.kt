package com.rex.lifetracker.viewModel.firebaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rex.lifetracker.model.FireBaseModel.UserInfoModel
import com.rex.lifetracker.repository.firebaseRepository.UserInfoRepository


class UserInfoViewModel(application: Application) : AndroidViewModel(application) {

    private val userInfoRepository: UserInfoRepository = UserInfoRepository()
    var insertResultLiveData: LiveData<String>? = null
    var getUserInfoLiveData: MutableLiveData<UserInfoModel?> = userInfoRepository.getUserInfoFromDataBase()

    fun insert(userinfo: UserInfoModel) {
        insertResultLiveData = userInfoRepository.insertUserInfoFirebase(userinfo)
    }


}