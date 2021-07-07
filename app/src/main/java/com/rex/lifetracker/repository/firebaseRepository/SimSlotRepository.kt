package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.model.FireBaseModel.SimSlotModel
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class SimSlotRepository {

    private val firebaseAuth = Firebase.auth.currentUser
    private val firebaseFirestore = Firebase.firestore.collection("Client")
        .document(firebaseAuth!!.uid).collection("Client_PersonalInfo").document("DEFAULT_SIM")

    fun insertSIMSlotToDatabase(simSlot: SimSlotModel): MutableLiveData<String>? {
        val insertResultLiveData = MutableLiveData<String>()

        GlobalScope.launch(Dispatchers.IO) {
          try {
              val data =  firebaseFirestore.set(simSlot).await()
              withContext(Dispatchers.Main){
                  insertResultLiveData.postValue("Successfully")
              }
          }catch (e:Exception){
              Log.d(TAG, "insertSIMSlotToDatabase: exception happen ${e.message}")
              withContext(Dispatchers.Main){
                  insertResultLiveData.postValue("Fail")
              }
          }
        }
        return insertResultLiveData
//        val simSlotModel = hashMapOf(
//
//            "SELECTED_SIM_SLOT" to simSlot.SELECTED_SIM_SLOT
//        )
//
//        firebaseFirestore.set(simSlotModel).addOnSuccessListener {
//            insertResultLiveData.value = "Successfully"
//        }.addOnFailureListener {
//            insertResultLiveData.value = "UnSuccessfully"
//        }
//
//        return insertResultLiveData
    }


    fun getSelectedSimSlot(): MutableLiveData<SimSlotModel?> {
        val getFireStoreMutableLiveData =
            MutableLiveData<SimSlotModel?>()
        GlobalScope.launch(Dispatchers.IO){
           try {
               val data = firebaseFirestore.get().await().toObject(SimSlotModel::class.java)
               withContext(Dispatchers.Main){
                   getFireStoreMutableLiveData.postValue(data)
               }
           }catch (e:Exception){
               withContext(Dispatchers.Main){
                   Log.d(TAG, "getSelectedSimSlot: exception happened ${e.message}")
                   getFireStoreMutableLiveData.postValue(SimSlotModel("0"))
               }
           }
        }
        return getFireStoreMutableLiveData
//        firebaseFirestore.get().addOnSuccessListener {
//
//            val simSlot = SimSlotModel(
//                it["SELECTED_SIM_SLOT"].toString()
//            )
//            getFireStoreMutableLiveData.value = simSlot
//        }.addOnFailureListener {
//            getFireStoreMutableLiveData.value = SimSlotModel(
//               "null"
//            )
//        }

    }
}