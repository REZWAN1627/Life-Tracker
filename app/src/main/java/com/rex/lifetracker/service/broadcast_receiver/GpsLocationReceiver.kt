package com.rex.lifetracker.service.broadcast_receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.location.LocationManager
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.MutableLiveData
import com.google.api.LogDescriptor
import com.rex.lifetracker.utils.Constant.TAG


class GpsLocationReceiver:BroadcastReceiver(){

    override fun onReceive(context: Context?, intent: Intent?) {
      //  Log.d(TAG, "onReceive: is called 1")

        intent?.action?.let { act ->
        //    Log.d(TAG, "onReceive: GPS is called")
            if (act.matches("android.location.PROVIDERS_CHANGED".toRegex())) {
              //  Log.d(TAG, "onReceive: Inside of GPS")
                val locationManager = context!!.getSystemService(LOCATION_SERVICE) as LocationManager


                //Start your Activity if location was enabled:
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                  //  Log.d(TAG, "onReceive: gps enable")
                    GPSEnable.postValue(true)
                }else{
                 //   Log.d(TAG, "onReceive: gps desable")
                    GPSEnable.postValue(false)
                }
            }
        }

    }

    companion object{
        val GPSEnable = MutableLiveData<Boolean>()
    }

}