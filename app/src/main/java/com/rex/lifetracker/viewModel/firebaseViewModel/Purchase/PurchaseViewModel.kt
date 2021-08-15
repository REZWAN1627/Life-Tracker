package com.rex.lifetracker.viewModel.firebaseViewModel.Purchase

import androidx.lifecycle.ViewModel
import com.rex.lifetracker.model.FireBaseModel.PurchaseModel.PurchaseModel
import com.rex.lifetracker.repository.firebaseRepository.PurchaseRepository

class PurchaseViewModel : ViewModel() {
    private val purchaseRepository = PurchaseRepository()

    fun insertData(
        purchaseModel: PurchaseModel, packageName: String
    ) {
        purchaseRepository.insertData(
            purchaseModel, packageName
        )
    }
}