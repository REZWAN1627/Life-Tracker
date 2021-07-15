package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.*
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class AppsInformationRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore =
        Firebase.firestore.collection("admin")


    fun getAppsInformation(): MutableLiveData<AppsAdminModel?> {
        val currentUser = firebaseAuth.currentUser!!.uid
        Log.d(TAG, "getAppsInformation: $currentUser")
        val getFireStoreMutableLiveData: MutableLiveData<AppsAdminModel?> =
            MutableLiveData<AppsAdminModel?>()

        GlobalScope.launch(Dispatchers.IO) {
            try {

               val info = firebaseFirestore.document("Information").get().await()
                    .toObject(InformationModel::class.java)
              val one =  firebaseFirestore.document("one_MonthPack").get().await()
                    .toObject(One_Month_Pack_Model::class.java)
             val six =   firebaseFirestore.document("six_MonthPack").get().await()
                    .toObject(Six_Month_Pack_Model::class.java)
              val twelve =  firebaseFirestore.document("twelve_Monthpack").get().await()
                    .toObject(Twelve_Month_Pack_Model::class.java)
                withContext(Dispatchers.Main){
                    getFireStoreMutableLiveData.postValue(AppsAdminModel(
                        info,one,six,twelve
                    ))
                }

            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d(TAG, "getAppsInformation: exception happened ${e.message}")
                    getFireStoreMutableLiveData.postValue(AppsAdminModel(
                        null,null,null,null
                    ))
                }
            }

        }
        return getFireStoreMutableLiveData


    }


}