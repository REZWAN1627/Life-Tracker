package com.rex.lifetracker.viewModel.firebaseViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rex.lifetracker.model.FireBaseModel.SimSlotModel
import com.rex.lifetracker.repository.firebaseRepository.SimSlotRepository

class SimSlotViewModel(application: Application) : AndroidViewModel(application) {

    private val simSlotRepo = SimSlotRepository()
    var insertSelectedSimSlot: LiveData<String>? = null
    var getSelectedSimSlot: MutableLiveData<SimSlotModel?> = simSlotRepo.getSelectedSimSlot()

    fun insertSimSlot(simSlotModel: SimSlotModel){
        insertSelectedSimSlot = simSlotRepo.insertSIMSlotToDatabase(simSlotModel)
    }

}