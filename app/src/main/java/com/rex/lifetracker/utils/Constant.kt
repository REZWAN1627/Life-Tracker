package com.rex.lifetracker.utils

import android.graphics.Color
import com.rex.lifetracker.R

object Constant {
    const val ACTION_START_SERVICE = "ACTION_START_OR_RESUME_SERVICE"
    const val ACTION_STOP_SERVICE = "ACTION_STOP_SERVICE"
    const val REQUEST_CODE_LOCATION_PERMISSION = 166


    const val REQUEST_PERMISSION = 5
    const val RC_SIGN_IN = 1
    const val TAG = "TAG"
    const val CHANNEL_ID = "Bike"
    const val REQUESTED_PERMISSION_CODE = 100
    const val ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 2323
    const val REQUEST_STORAGE_READ_WRITE_CODE = 500

    const val CANCEL_ACTION = "CANCEL_ACTION"
    const val CANCEL_ACTION2 = "CANCEL_ACTION"
    const val STOP_SERVICE_ACTION = "STOP_SERVICE_ACTION"
    const val CHANNEL_ALERT_SYSTEM_ID = "CHANNEL_ALERT_SYSTEM_ID"
    const val CHANNEL_ALERT2_SYSTEM_ID = "CHANNEL_ALERT2_SYSTEM_ID"
    const val BROADCAST_REQUEST_CODE = 200
    const val BROADCAST_REQUEST_CODE2 = 201
    const val ACTIVITY_REQUEST_CODE = 0
    const val FOREGROUND_NOTIFICATION_ID = 1
    const val MOTION_ALERT_SYSTEM_NOTIFICATION_ID = 2
    const val MOTION_ALERT_SYSTEM_NOTIFICATION_ID2 = 3
    const val GPS_AUTO_START_REQUEST_CODE = 1627
    const val DEFAULT_IMAGE_URI = R.drawable.defaultimage
    const val GPS_PERMISSION_CODE = 2566


    //expreriment
    const val ACTION_START_OR_RESUME_SERVICE = "ACTION_START_OR_RESUME_SERVICES"
    const val ACTION_PAUSE_SERVICE = "ACTION_PAUSE_SERVICE"
    const val ACTION_STOP_SERVICES = "ACTION_STOP_SERVICES"
    const val NOTIFICATION_CHANNEL_ID = "tracking_channel"
    const val NOTIFICATION_CHANNEL_NAME = "Tracking"
    const val NOTIFICATION_ID = 1
    const val LOCATION_UPDATE_INTERVAL = 5000L
    const val FASTEST_LOCATION_INTERVAL = 2000L
    const val POLYLINE_COLOR = Color.RED
    const val POLYLINE_WIDTH = 8f
    const val MAP_ZOOM = 17f
}