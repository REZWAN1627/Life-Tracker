package com.rex.lifetracker.viewModel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.rex.lifetracker.model.contactModel.ContactsReadModel
import com.rex.lifetracker.repository.ContactsReadRepo

class ContactsReadViewModel : ViewModel() {

    private var contactsReadRepo = ContactsReadRepo()
    var contactsReadList = MutableLiveData<ContactsReadModel>()

    fun readAllContactsList(context: Context) {
        contactsReadList = contactsReadRepo.getAllContacts(context)
    }
}