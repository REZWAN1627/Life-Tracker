package com.rex.lifetracker.Network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.MutableLiveData


class NetworkAvailable {


    fun isNetworkAvailable(context: Context): MutableLiveData<Boolean>? {

        val mutableLiveData = MutableLiveData<Boolean>()

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val nw = connectivityManager.activeNetwork
            if (nw == null) {
                mutableLiveData.value = false
            } else {
                val actNw: NetworkCapabilities? = connectivityManager.getNetworkCapabilities(nw)

                if (actNw != null) {
                    when {
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                            mutableLiveData.value = true
                        }
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                            mutableLiveData.value = true
                        }
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                            mutableLiveData.value = true
                        }
                        actNw.hasTransport(NetworkCapabilities.TRANSPORT_BLUETOOTH) -> {
                            mutableLiveData.value = true
                        }
                        else -> {
                            mutableLiveData.value = false
                        }
                    }

                } else {
                    mutableLiveData.value = false
                }
            }


        } else {
            val nwInfo = connectivityManager.activeNetworkInfo
            mutableLiveData.value = nwInfo != null && nwInfo.isConnected
        }

        return mutableLiveData

    }

}