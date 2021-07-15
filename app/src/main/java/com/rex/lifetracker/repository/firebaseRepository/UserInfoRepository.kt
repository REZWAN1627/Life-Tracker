package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rex.lifetracker.model.FireBaseModel.UserInfoModel
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


class UserInfoRepository {

    private val firebaseAuth = Firebase.auth.currentUser?.uid
    private val firebaseFirestore =
        Firebase.firestore.collection("Client").document(firebaseAuth.toString())
            .collection("Client_PersonalInfo").document("Info")

    private val storageRef =
        Firebase.storage.reference.child("Images").child(firebaseAuth.toString()).child("Avatar")

    fun insertUserInfoFirebase(user: UserInfoModel, image: ByteArray?): MutableLiveData<String>? {
        Log.d(TAG, "insertUserInfoFirebase: is called")
        val insertResultLiveData = MutableLiveData<String>()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "insertUserInfoFirebase: is called in background")
                if (image != null) {
                    val result = storageRef.putBytes(image).await()
                    val uri = result.storage.downloadUrl.await().toString()

                    val data = UserInfoModel(
                        user.active_Time,
                        uri,
                        user.brought_pack_time,
                        user.deactivate_Time,
                        user.first_Name,
                        user.last_Name,
                        user.status,
                        user.subscription_pack
                    )
                    firebaseFirestore.set(data).await()
                    withContext(Dispatchers.IO) {
                        Log.d(TAG, "insertUserInfoFirebase: is done")
                        insertResultLiveData.postValue("Insert Successfully")
                    }

                } else {
                    firebaseFirestore.set(user).await()
                    withContext(Dispatchers.IO) {
                        insertResultLiveData.postValue("Insert Successfully")
                    }
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "insertUserInfoFirebase: exception happen ${e.message}")
                    insertResultLiveData.postValue("failed")
                }
            }
        }
        return insertResultLiveData


    }

    fun getUserInfoFromDataBase(): MutableLiveData<UserInfoModel?> {


        Log.d(TAG, "getUserInfoFromDataBase: uid $firebaseAuth")
        val getFireStoreMutableLiveData =
            MutableLiveData<UserInfoModel?>()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                Log.d(TAG, "getUserInfoFromDataBase: getting value")
                val data = firebaseFirestore.get().await().toObject(UserInfoModel::class.java)
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "getUserInfoFromDataBase: $data")
                    getFireStoreMutableLiveData.postValue(data)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.IO) {
                    Log.d(TAG, "getUserInfoFromDataBase: exception happened ${e.message}")
                    getFireStoreMutableLiveData.postValue(
                        UserInfoModel(
                            "null", "null",
                            "null", "null",
                            "null", "null", "null", "null"
                        )
                    )
                }

            }

        }
        return getFireStoreMutableLiveData


    }
}