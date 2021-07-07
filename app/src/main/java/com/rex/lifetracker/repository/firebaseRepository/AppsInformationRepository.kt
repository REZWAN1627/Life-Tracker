package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.model.FireBaseModel.AppsInformationModel
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppsInformationRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore =
        Firebase.firestore.collection("admin").document("OCMq9Y0bE3XkE4pSFC4IxOjkIKw2")


    fun getAppsInformation(): MutableLiveData<AppsInformationModel?> {
        val currentUser = firebaseAuth.currentUser!!.uid
        Log.d(TAG, "getAppsInformation: $currentUser")
        val getFireStoreMutableLiveData: MutableLiveData<AppsInformationModel?> =
            MutableLiveData<AppsInformationModel?>()

        GlobalScope.launch(Dispatchers.IO) {
            try {
                val data =
                    firebaseFirestore.get().await().toObject(AppsInformationModel::class.java)
                withContext(Dispatchers.Main) {
                    getFireStoreMutableLiveData.postValue(data)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "getAppsInformation: exception happened ${e.message}")
                    getFireStoreMutableLiveData.postValue(
                        AppsInformationModel(
                            "null", "null", "null", "null", "null",
                            "null"
                        )
                    )
                }
            }

        }
        return getFireStoreMutableLiveData


//        firebaseFirestore.collection("admin").document("OCMq9Y0bE3XkE4pSFC4IxOjkIKw2")
//            .get()
//            .addOnSuccessListener {
//                Log.d(TAG, "getAppsInfoFromDataBase: ${it["info"].toString()}")
//                val user = AppsInformationModel(
//
//                    it["info"].toString(),
//                    it["bought_date_time"].toString(),
//                    it["cost"].toString(),
//                    it["monthly_sub_pack"].toString(),
//                    it["plan"].toString(),
//                    it["status"].toString()
//
//                )
//                getFireStoreMutableLiveData.setValue(user)
//            }.addOnFailureListener { exception ->
//                Log.d(TAG, "get failed with ", exception)
//            }
//        return getFireStoreMutableLiveData
    }


}