package com.rex.lifetracker.repository.firebaseRepository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.model.FireBaseModel.PurchaseModel.PurchaseModel
import com.rex.lifetracker.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class PurchaseRepository {
    private val firebaseFirestore = Firebase.firestore.collection("Purchase")

    fun insertData(
        purchaseModel: PurchaseModel,
        packageName: String
    ) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                firebaseFirestore.document(packageName).set(purchaseModel).await()

            } catch (e: Exception) {
                Log.d(Constant.TAG, "insertData: exception happen ${e.message}")
            }
        }
    }
}