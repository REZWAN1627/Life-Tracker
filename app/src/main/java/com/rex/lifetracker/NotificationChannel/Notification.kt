package com.rex.lifetracker.NotificationChannel

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import androidx.core.app.NotificationCompat
import com.rex.lifetracker.R
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT2_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ID_CALLING
import com.rex.lifetracker.utils.Constant.WOMEN_SAFETY_CHANNEL_ID


class Notification : Application() {
    override fun onCreate() {
        super.onCreate()
        foreGroundService()
        backGroundHighAlertService()
        backGroundLowAlertService()
        womenSafetyAlert()
        createPhoneService()
    }

    private fun createPhoneService() {

        val serviceChannel = NotificationChannel(
            CHANNEL_ID_CALLING,
            "Calling",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        serviceChannel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            audioAttributes
        )
        serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager.createNotificationChannel(serviceChannel)

    }

    private fun womenSafetyAlert() {
        val serviceChannel = NotificationChannel(
            WOMEN_SAFETY_CHANNEL_ID,
            "Women Safety Alert",
            NotificationManager.IMPORTANCE_HIGH
        )
        serviceChannel.description = "5 second reminder"
        val manager = getSystemService(
            NotificationManager::class.java
        )
        serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        serviceChannel.setSound(null, null)
        manager.createNotificationChannel(serviceChannel)
    }

    private fun backGroundLowAlertService() {


        val serviceChannel = NotificationChannel(
            CHANNEL_ALERT2_SYSTEM_ID,
            "Alert System When app in foreground",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        serviceChannel.description = "30 second reminder"
        val manager = getSystemService(
            NotificationManager::class.java
        )

        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.siren)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

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
        serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        serviceChannel.setSound(
            sound,
            audioAttributes
        )
        manager.createNotificationChannel(serviceChannel)
    }

    private fun backGroundHighAlertService() {
        val serviceChannel = NotificationChannel(
            CHANNEL_ALERT_SYSTEM_ID,
            "Alert System When app in background",
            NotificationManager.IMPORTANCE_HIGH
        )
        serviceChannel.description = "30 second reminder"
        val manager = getSystemService(
            NotificationManager::class.java
        )
        val sound =
            Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + packageName + "/" + R.raw.siren)
        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

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
        serviceChannel.lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
        serviceChannel.shouldVibrate()
        serviceChannel.setSound(
            sound,
            audioAttributes
        )
        manager.createNotificationChannel(serviceChannel)

    }

    private fun foreGroundService() {

        val serviceChannel = NotificationChannel(
            CHANNEL_ID,
            "Map and Sensor Notification",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        val manager = getSystemService(
            NotificationManager::class.java
        )
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .build()
        serviceChannel.setSound(
            RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION),
            audioAttributes
        )
        serviceChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        manager.createNotificationChannel(serviceChannel)

    }
}