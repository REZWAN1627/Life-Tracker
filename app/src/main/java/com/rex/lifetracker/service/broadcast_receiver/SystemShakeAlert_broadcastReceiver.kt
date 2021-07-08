package com.rex.lifetracker.service.broadcast_receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.Toast
import com.rex.lifetracker.service.MotionDetectService
import com.rex.lifetracker.utils.Constant.CANCEL_ACTION
import com.rex.lifetracker.utils.Constant.TAG

class SystemShakeAlert_broadcastReceiver :BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        when(intent?.action){
            CANCEL_ACTION ->{
                Log.d(TAG, "onReceive: is called")
                Toast.makeText(context, "Serviced Canceled", Toast.LENGTH_SHORT).show()
                val notificationManager =
                    context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(2)
                context.startForegroundService(Intent(context,MotionDetectService::class.java))

            }
        }

    }
}