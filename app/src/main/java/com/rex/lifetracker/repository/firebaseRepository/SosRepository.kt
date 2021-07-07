package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.rex.lifetracker.model.FireBaseModel.SosModel
import com.rex.lifetracker.utils.Constant.TAG


class SosRepository {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseFirestore = FirebaseFirestore.getInstance()

    fun insertSOSContactsFirebase(user: SosModel): MutableLiveData<String>? {
        val currentUser = firebaseAuth.currentUser?.uid
        val insertResultLiveData = MutableLiveData<String>()

        val SOS_PhoneNumbers = hashMapOf(
            "FirstPersonName" to user.FirstPersonName,
            "First_personContactsNumber" to user.First_personContactsNumber,
            "Image1" to user.Image1,
            "Image2" to user.Image2,
            "SecondPersonName" to user.SecondPersonName,
            "Second_personContactsNumber" to user.Second_personContactsNumber


        )
        if (currentUser != null) {
            firebaseFirestore.collection("Client").document(currentUser)
                .collection("Client_PersonalInfo").document("SOS_Contacts")
                .set(SOS_PhoneNumbers)
                .addOnSuccessListener { insertResultLiveData.setValue("Insert Successfully") }
                .addOnFailureListener { e -> insertResultLiveData.setValue(e.toString()) }
            return insertResultLiveData
        } else {
            Log.d(TAG, "insertSOSContactsFirebase: current user is null")
        }
        return insertResultLiveData

    }

    fun getSOSContactsFromDataBase(): MutableLiveData<List<SosModel>> {
        val currentUser = firebaseAuth.currentUser!!.uid
        val listOfUserInfo: MutableList<SosModel> = mutableListOf<SosModel>()
        val getFireStoreMutableLiveData: MutableLiveData<List<SosModel>> =
            MutableLiveData<List<SosModel>>()

        firebaseFirestore.collection("Client").document(currentUser)
            .collection("Client_PersonalInfo").document("SOS_Contacts").get()
            .addOnSuccessListener {
                Log.d(TAG, "getUserInfoFromDataBase: ${it.data}")
                val user = SosModel(
                    it["FirstPersonName"].toString(),
                    it["First_personContactsNumber"].toString(),
                    it["Image1"].toString(),
                    it["Image2"].toString(),
                    it["SecondPersonName"].toString(),
                    it["Second_personContactsNumber"].toString()
                )
                listOfUserInfo.add(user)
                getFireStoreMutableLiveData.setValue(listOfUserInfo);
            }.addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
        return getFireStoreMutableLiveData
    }
}