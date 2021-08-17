package com.rex.lifetracker.repository.firebaseRepository

import android.content.Context
import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.rex.lifetracker.utils.Constant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.File

class DownloadXLRepository {

    private val allDir = Firebase.storage.reference.child("ListOfDivition")
    private val individualFile = Firebase.storage.reference.child("ListOfDivition")
    private var string = ""
    private lateinit var localFile: File

    fun downloadXLSheet(context: Context){
        val file = context.getExternalFilesDir("EXCEL")
        if (!file!!.exists()){
            file!!.mkdir()
        }

        GlobalScope.launch (Dispatchers.IO){
            val result = allDir.listAll().await()
            for (item in result.items){
                string = item.toString()
                val list = string.split("/")
                Log.d(Constant.TAG, "onCreate: ${list[list.size-1]}")
                localFile= File(file, "${list[list.size-1]}")
                individualFile.child("${list[list.size-1]}")
                    .getFile(localFile).await()
            }
        }

    }

}