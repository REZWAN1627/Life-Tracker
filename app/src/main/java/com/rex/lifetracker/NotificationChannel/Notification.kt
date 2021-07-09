package com.rex.lifetracker.NotificationChannel

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import com.rex.lifetracker.R
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT2_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ID


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        createSecondNotificationChannel()
        createThirdNotificationChannel()
    }

    private fun createThirdNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ALERT2_SYSTEM_ID,
            "ALERT SYSTEM",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        serviceChannel.description = "SYSTEM ALERT"
        val manager = getSystemService(
            NotificationManager::class.java
        )
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val soundUri = Uri.parse(
            "android.resource://"
                    + packageName + "/" + R.raw.siren
        )

        serviceChannel.vibrationPattern = longArrayOf(
            1000,
            1000,
            1000,
            1000,
            1000,
            1000,
            1000,
            1000


        )
        serviceChannel.enableVibration(true)
        serviceChannel.shouldVibrate()

        serviceChannel.setSound(
            soundUri,
            audioAttributes
        )
        manager.createNotificationChannel(serviceChannel)
    }

    private fun createSecondNotificationChannel() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ALERT_SYSTEM_ID,
            "ALERT SYSTEM",
            NotificationManager.IMPORTANCE_HIGH
        )
        serviceChannel.description = "SYSTEM ALERT"
        val manager = getSystemService(
            NotificationManager::class.java
        )
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()
        val soundUri = Uri.parse(
            "android.resource://"
                    + packageName + "/" + R.raw.siren
        )

        serviceChannel.vibrationPattern = longArrayOf(
            1000,
            1000,
            1000,
            1000,
            1000,
            1000,
            1000,
            1000


        )
        serviceChannel.enableVibration(true)
        serviceChannel.shouldVibrate()

        serviceChannel.setSound(
            soundUri,
            audioAttributes
        )
        manager.createNotificationChannel(serviceChannel)

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Example Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(
                NotificationManager::class.java
            )
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_ALARM)
                .build()
            serviceChannel.setSound(
                RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
                audioAttributes
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }
}