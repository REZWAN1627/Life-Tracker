package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.model.FireBaseModel.TrustedContactsModel
import com.rex.lifetracker.utils.Constant.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception

class TrustedContactsRepository {

    private val firebaseAuth = Firebase.auth.currentUser
    private val firebaseFirestore = Firebase.firestore.collection("Client")
        .document(firebaseAuth!!.uid).collection("Trusted_Contact")
    private val storageRef = Firebase.storage.reference.child("Images").child(firebaseAuth!!.uid)


    fun insertTrustedContactsInfo(trustedContactsModel: TrustedContactsModel): MutableLiveData<String> {

        val insertResultLiveData = MutableLiveData<String>()
        GlobalScope.launch (Dispatchers.IO){
            try {
                firebaseFirestore.document(trustedContactsModel.Phone).set(trustedContactsModel).await()
                withContext(Dispatchers.Main){
                    insertResultLiveData.postValue("Success")
                }
            }catch (e:Exception){
                Log.d(TAG, "insertTrustedContactsInfo: exception happen ${e.message}")
                withContext(Dispatchers.Main){
                    insertResultLiveData.postValue("Failed")
                }
            }
        }
        return insertResultLiveData

    }


    fun getTrustedContactsInfo(): MutableLiveData<List<TrustedContactsModel>> {
        Log.d(TAG, "getTrustedContactsInfo: is called")
        val listOfNumbers = mutableListOf<TrustedContactsModel>()
        val getFireStoreMutableLiveData =
            MutableLiveData<List<TrustedContactsModel>>()

        GlobalScope.launch(IO) {
          try {
              val data = firebaseFirestore.get().await().toObjects(TrustedContactsModel::class.java)
              withContext(Main){
                  getFireStoreMutableLiveData.postValue(data)
              }
          }catch (e:Exception){
              withContext(Main){
                  Log.d(TAG, "getTrustedContactsInfo: exception happened ${e.message} ")
                  getFireStoreMutableLiveData.postValue(emptyList())
              }
          }
        }
        return  getFireStoreMutableLiveData


    }

    fun deleteTrustedContacts(deleteContact: String) {
        GlobalScope.launch (IO){
           try {
               val result = firebaseFirestore.document(deleteContact).delete().await()
               storageRef.child(deleteContact).delete().await()
                   Log.d(TAG, "deleteTrustedContacts: $result")

           }catch (e:Exception){
               Log.d(TAG, "deleteTrustedContacts: exception happened ${e.message}")
           }
        }

    }

    fun updateDataTOFireBase(model: SOSContacts_Entity, image: ByteArray?) {

        GlobalScope.launch(Dispatchers.IO) {

            try {
                if (image != null) {
                    val result = storageRef.child(model.Phone).putBytes(image).await()
                    val uri = result.storage.downloadUrl.await().toString()
                    val trustedContactsInfo = hashMapOf(
                        "Image" to uri,
                        "Name" to model.Name,
                        "Phone" to model.Phone,
                        "Priority" to model.Priority,
                    )
                    firebaseFirestore.document(model.Phone).set(trustedContactsInfo).await()
                }else{

                    val trustedContactsInfo = hashMapOf(
                        "Image" to "null",
                        "Name" to model.Name,
                        "Phone" to model.Phone,
                        "Priority" to model.Priority,
                    )
                    firebaseFirestore.document(model.Phone).set(trustedContactsInfo).await()

                }
            }catch (e:Exception){
                Log.d(TAG, "updateDataTOFireBase: exception happen ${e.message}")
            }

        }

    }


//    fun getSelectedTrustedNumber(): MutableLiveData<List<TrustedContactsModel>> {
//        val listOfNumbers = mutableListOf<TrustedContactsModel>()
//        val getFireStoreMutableLiveData =
//            MutableLiveData<List<TrustedContactsModel>>()
//        firebaseFirestore.whereNotEqualTo("Priority", "null").get().addOnSuccessListener {
//            for (document in it.documents) {
//                listOfNumbers.add(document.toObject<TrustedContactsModel>()!!)
//            }
//            Log.d(TAG, "getTrustedContactsInfo: size --> " + listOfNumbers.size)
//            getFireStoreMutableLiveData.value = listOfNumbers
//        }.addOnFailureListener {
//            Log.d(TAG, "getSelectedTrustedNumber: fail ${it.message}")
//            val firebase = Firebase.auth
//            firebase.signOut()
//        }
//
//        return getFireStoreMutableLiveData
//    }


}