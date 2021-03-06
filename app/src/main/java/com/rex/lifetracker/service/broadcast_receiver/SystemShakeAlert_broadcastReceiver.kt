package com.rex.lifetracker.service.broadcast_receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast
import com.rex.lifetracker.service.CallingServices
import com.rex.lifetracker.service.MotionDetectService
import com.rex.lifetracker.utils.Constant
import com.rex.lifetracker.utils.Constant.CANCEL_ACTION
import com.rex.lifetracker.utils.Constant.START_PHONE_SERVICES
import com.rex.lifetracker.utils.Constant.STOP_SERVICE_ACTION
import com.rex.lifetracker.utils.Constant.STOP_SERVICE_ACTION_CALL
import com.rex.lifetracker.utils.Constant.TAG

class SystemShakeAlert_broadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {

        when (intent?.action) {
            CANCEL_ACTION -> {
                Log.d(TAG, "onReceive: is called shake")
                Toast.makeText(context, "Serviced Canceled", Toast.LENGTH_SHORT).show()
                val notificationManager =
                    context!!.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancelAll()
                context.startService(Intent(context, MotionDetectService::class.java).apply {
                    this.action = Constant.ACTION_START_SERVICE
                })

            }
            STOP_SERVICE_ACTION -> {
                context?.startService(Intent(context, MotionDetectService::class.java).apply {
                    this.action = Constant.ACTION_STOP_SERVICE
                })
            }
            STOP_SERVICE_ACTION_CALL -> {
                context?.stopService(Intent(context, CallingServices::class.java))
                context?.startService(Intent(context, MotionDetectService::class.java).apply {
                    this.action = Constant.ACTION_START_SERVICE
                })
            }
            START_PHONE_SERVICES -> {
//                context?.stopService(Intent(context, MotionDetectService::class.java))
                context?.startService(Intent(context, CallingServices::class.java))
            }


        }

    }
}