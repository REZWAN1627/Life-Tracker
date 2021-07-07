package com.rex.lifetracker.repository

import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.lifecycle.MutableLiveData
import com.rex.lifetracker.model.contactModel.ContactsReadModel

class ContactsReadRepo {


    fun getAllContacts(context: Context): MutableLiveData<ContactsReadModel> {

        val contactList = MutableLiveData<ContactsReadModel>()
        var cursor: Cursor? = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null, null, null,
            null
        )
        while (cursor!!.moveToNext()) {

            contactList.value = ContactsReadModel(
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)),
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)),
                cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI))
            )
        }

        return contactList
    }
}