package com.rex.lifetracker.service

import android.annotation.SuppressLint
import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.media.AudioManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

import androidx.lifecycle.*
import androidx.media.VolumeProviderCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationRequest.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.location.LocationResult
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.rex.lifetracker.R
import com.rex.lifetracker.service.broadcast_receiver.SystemShakeAlert_broadcastReceiver
import com.rex.lifetracker.utils.Constant.ACTION_START_SERVICE
import com.rex.lifetracker.utils.Constant.ACTION_STOP_SERVICE
import com.rex.lifetracker.utils.Constant.ACTION_WOMEN_SAFETY_SERVICE
import com.rex.lifetracker.utils.Constant.BROADCAST_REQUEST_CODE
import com.rex.lifetracker.utils.Constant.CANCEL_ACTION
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT2_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ID
import com.rex.lifetracker.utils.Constant.FASTEST_LOCATION_INTERVAL
import com.rex.lifetracker.utils.Constant.FOREGROUND_NOTIFICATION_ID
import com.rex.lifetracker.utils.Constant.LOCATION_UPDATE_INTERVAL
import com.rex.lifetracker.utils.Constant.MOTION_ALERT_SYSTEM_NOTIFICATION_ID
import com.rex.lifetracker.utils.Constant.MOTION_ALERT_SYSTEM_NOTIFICATION_ID2
import com.rex.lifetracker.utils.Constant.START_PHONE_SERVICES
import com.rex.lifetracker.utils.Constant.STOP_SERVICE_ACTION
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.Constant.WOMEN_SAFETY_CHANNEL_ID
import com.rex.lifetracker.utils.Constant.WOMEN_SAFETY_NOTIFICATION_ID
import com.rex.lifetracker.utils.Permission
import kotlin.math.roundToInt
import kotlin.math.sqrt


typealias Polyline = MutableList<LatLng>
typealias Polylines = MutableList<Polyline>

class MotionDetectService : LifecycleService(), SensorEventListener, LifecycleObserver{
    //detect apps forground and background state
    var wasInBackground = false




    //sensor ans notification
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private lateinit var notificationManager: NotificationManagerCompat


    private var accelerationX = 0.0
    private var accelerationY: Double = 0.0
    private var accelerationZ: Double = 0.0

    //accident threashold
    private val threshold = 65

    // Minimum acceleration needed to count as a shake movement
    private val MIN_SHAKE_ACCELERATION = 12

    // Minimum number of movements to register a shake
    private val MIN_MOVEMENTS = 1

    // Maximum time (in milliseconds) for the whole shake to occur
    private val MAX_SHAKE_DURATION = 500

    // Arrays to store gravity and linear acceleration values
    private val mGravity = floatArrayOf(0.0f, 0.0f, 0.0f)
    private val mLinearAcceleration = floatArrayOf(0.0f, 0.0f, 0.0f)
    private var Current = 0.0f

    // Indexes for x, y, and z values
    private val X = 0
    private val Y = 1
    private val Z = 2

    // Start time for the shake detection
    var startTime: Long = 0

    // Counter for shake movements
    var moveCount = 0
    //------------------sensor declatarion-----------///


    //----------------------googleMap------------------------//


    lateinit var fusedLocationProviderClient: FusedLocationProviderClient


    private var mediaSession: MediaSessionCompat? = null
    private var volumePressed = 0


    companion object {
        val isTracking = MutableLiveData<Boolean>()
        val pathPoints = MutableLiveData<Polylines>()
        val uiChange = MutableLiveData<UIChange>()

    }

    private fun postInitialValues() {
        isTracking.postValue(false)
        pathPoints.postValue(mutableListOf())

    }

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
        uiChange.postValue(UIChange.END)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        notificationManager = NotificationManagerCompat.from(this)
        postInitialValues()
        fusedLocationProviderClient = FusedLocationProviderClient(this)
        isTracking.observe(this, Observer {
            //  Log.d(TAG, "onCreate: value of tracking: $it")
            updateLocationTracking(it)
        })

        volumeButtonPressed()




    }

    private fun volumeButtonPressed() {
        mediaSession = MediaSessionCompat(this, "PlayerService")
        mediaSession!!.setFlags(
            MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                    MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
        )
        mediaSession!!.setPlaybackState(
            PlaybackStateCompat.Builder()
                .setState(
                    PlaybackStateCompat.STATE_PLAYING,
                    0,
                    0f
                ) //you simulate a player which plays something.
                .build()
        )
        val myVolumeProvider = object : VolumeProviderCompat(
            VolumeProviderCompat.VOLUME_CONTROL_RELATIVE, /*max volume*/
            100, /*initial volume level*/
            50
        ) {
            @SuppressLint("InvalidWakeLockTag")
            override fun onAdjustVolume(direction: Int) {
                Log.e("ACTION", direction.toString())
                Log.d(TAG, "onAdjustVolume: $direction")
                Log.d(TAG, "onAdjustVolume: :count $volumePressed")
                volumePressed++
                if (volumePressed >= 4) {
                    womenSafety()
                    Log.e(TAG, "onAdjustVolume: Working")
                }

            }
        }
        mediaSession!!.setPlaybackToRemote(myVolumeProvider)
        mediaSession!!.isActive = true
    }

    //-------------------google map lines-------------------------------//
    @SuppressLint("MissingPermission")
    private fun updateLocationTracking(isTracking: Boolean) {
        //   Log.d(TAG, "updateLocationTracking: is called")
        if (isTracking) {
            //   Log.d(TAG, "updateLocationTracking: is in")
            if (Permission.hasLocationPermissions(this)) {
                //       Log.d(TAG, "updateLocationTracking: in 2")
                val request = LocationRequest.create().apply {
                    interval = LOCATION_UPDATE_INTERVAL
                    fastestInterval = FASTEST_LOCATION_INTERVAL
                    priority = PRIORITY_HIGH_ACCURACY
                }
                fusedLocationProviderClient.requestLocationUpdates(
                    request,
                    locationCallback,
                    Looper.getMainLooper()
                )
//                map?.uiSettings?.isMyLocationButtonEnabled = true
//                map?.isMyLocationEnabled = true
            }
        } else {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        }
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult?) {
            super.onLocationResult(result)


            if (isTracking.value!!) {
                //     Log.d(TAG, "onLocationResult: is called")
                result?.locations?.let { locations ->
                    for (location in locations) {
                        addPathPoint(location)
//                        Log.d(
//                            TAG,
//                            "onLocationResult: NEW LOCATION: ${location.latitude}, ${location.longitude}"
//                        )
                    }
                }

            }
        }
    }



    private fun addPathPoint(location: Location?) {
        location?.let {
            val pos = LatLng(location.latitude, location.longitude)
            pathPoints.value?.apply {
                last().add(pos)
                pathPoints.postValue(this)
            }
        }
    }

    private fun addEmptyPolyline() = pathPoints.value?.apply {
        add(mutableListOf())
        pathPoints.postValue(this)
    } ?: pathPoints.postValue(mutableListOf(mutableListOf()))

    //---------------------------------Google Map ------------------------//

    //-----------sensor--------------------------------------------//

    override fun onSensorChanged(event: SensorEvent?) {


        accelerationX = (event!!.values[0] * 1000).roundToInt() / 1000.0
        // Log.d(TAG, "onSensorChanged: acceleration X -------> $accelerationX")
        accelerationY = (event.values[1] * 1000).roundToInt() / 1000.0
        //Log.d(TAG, "onSensorChanged: acceleration Y -------> $accelerationY")
        accelerationZ = (event.values[2] * 1000).roundToInt() / 1000.0
        // Log.d(TAG, "onSensorChanged: acceleration Z -------> $accelerationZ")

//        Log.d(TAG, "===============================================================")
//        Log.d(TAG, "===============================================================")

        /* Detect Accident */
        if (accelerationX > threshold || accelerationY > threshold || accelerationZ > threshold) {
//            Log.d(TAG, "threshold == $threshold")
//            Log.d(TAG, "final: acceleration X -------> $accelerationX")
//            Log.d(TAG, "final: acceleration Y -------> $accelerationY")
//            Log.d(TAG, "final: acceleration Z -------> $accelerationZ")
            executeShakeAction()
        }

        // This method will be called when the accelerometer detects a change.

        // Call a helper method that wraps code from the Android developer site
        setCurrentAcceleration(event)

        // Get the max linear acceleration in any direction
        val maxLinearAcceleration = getMaxCurrentLinearAcceleration()

        // Check if the acceleration is greater than our minimum threshold
        if (maxLinearAcceleration > MIN_SHAKE_ACCELERATION) {
            val now = System.currentTimeMillis()

            // Set the startTime if it was reset to zero
            if (startTime == 0L) {
                startTime = now
            }
            val elapsedTime = now - startTime

            // Check if we're still in the shake window we defined
            if (elapsedTime > MAX_SHAKE_DURATION) {
                // Too much time has passed. Start over!
                resetShakeDetection()
            } else {
                // Keep track of all the movements
                moveCount++

                // Check if enough movements have been made to qualify as a shake
                if (moveCount > MIN_MOVEMENTS) {

                    // Reset for the next one!
                    resetShakeDetection()

                }
            }
        }

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }


    private fun setCurrentAcceleration(event: SensorEvent) {
        /*
         *  BEGIN SECTION from Android developer site. This code accounts for
         *  gravity using a high-pass filter
         */

        // alpha is calculated as t / (t + dT)
        // with t, the low-pass filter's time-constant
        // and dT, the event delivery rate
        val alpha = 0.8f

        // Gravity components of x, y, and z acceleration
        mGravity[X] =
            alpha * mGravity[X] + (1 - alpha) * event.values[X]
        mGravity[Y] =
            alpha * mGravity[Y] + (1 - alpha) * event.values[Y]
        mGravity[Z] =
            alpha * mGravity[Z] + (1 - alpha) * event.values[Z]

        // Linear acceleration along the x, y, and z axes (gravity effects removed)
        mLinearAcceleration[X] =
            event.values[X] - mGravity[X]
        mLinearAcceleration[Y] =
            event.values[Y] - mGravity[Y]
        mLinearAcceleration[Z] =
            event.values[Z] - mGravity[Z]

        /*
         *  END SECTION from Android developer site
         */
//        Log.d(TAG, "===============================================================")
//        Log.d(TAG, "===============================================================")
        Current =
            sqrt(
                (mLinearAcceleration[X] * mLinearAcceleration[X] + mLinearAcceleration[Y] *
                        mLinearAcceleration[Y] + mLinearAcceleration[Z] * mLinearAcceleration[Z]).toDouble()
            ).roundToInt()
                .toFloat()
        //  Log.d(TAG, "setCurrentAcceleration: current ---- $Current")
    }

    private fun getMaxCurrentLinearAcceleration(): Float {
        // Start by setting the value to the x value

        // Return the greatest value
        return Current
    }

    private fun resetShakeDetection() {
        startTime = 0
        moveCount = 0
    }

    private fun executeShakeAction() {
        // Toast.makeText(this, "Shake detected", Toast.LENGTH_SHORT).show()

        //   Log.d(TAG, "executeShakeAction: working")

        createAlertNotification()
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        stopSelf()
    }
    //--------------------sensor-------------------------//


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        intent?.let {
            when (it.action) {
                ACTION_START_SERVICE -> {
                    //timerEvent.postValue(UIChange.START)
                    ForeGroundStart()
                    uiChange.postValue(UIChange.END)


                    //   Log.d(TAG, "onStartCommand: Service is Started")
                }
                ACTION_STOP_SERVICE -> {
                    uiChange.postValue(UIChange.END)
                    sensorManager?.unregisterListener(this)
                    isTracking.postValue(false)
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback)
                    stopForeground(true)
                    postInitialValues()
                    stopSelf()
                }

            }
        }



        return super.onStartCommand(intent, flags, startId)
    }

    private fun womenSafety() {
        mediaSession!!.isActive = false
        sensorManager?.unregisterListener(this)
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
        //
        stopSelf()

        val pendingIntentCancel2 = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val pendingIntentCancel = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = CANCEL_ACTION
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val pendingIntentCancel3 = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = START_PHONE_SERVICES
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )


        val notification = NotificationCompat.Builder(this, WOMEN_SAFETY_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
            .setContentTitle("Danger Detected")
            .setContentText("After 10 Second the service will make calls")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .addAction(R.color.RED, "Cancel", pendingIntentCancel)
            .setDeleteIntent(pendingIntentCancel3)
            .setWhen(System.currentTimeMillis())
            .setUsesChronometer(true)
            .setFullScreenIntent(pendingIntentCancel2, true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setShowWhen(true)
            .setTimeoutAfter(10000)
            .build()
        notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
        notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
        notificationManager.notify(WOMEN_SAFETY_NOTIFICATION_ID, notification)
    }

    private fun ForeGroundStart() {
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        addEmptyPolyline()
        isTracking.postValue(true)


        val stopService = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = STOP_SERVICE_ACTION
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Life Tracker is Activated")
            .setContentText("Drive Safe, Keep your eyes on the road")
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
            .addAction(R.color.RED, "Stop Life Tracking", stopService)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            // .setContentIntent(pendingIntent)
            .build()
        notification.visibility = Notification.VISIBILITY_PUBLIC
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
    }


    private fun createAlertNotification() {
        uiChange.postValue(UIChange.START)
        sensorManager?.unregisterListener(this)

        val pendingIntentCancel = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = CANCEL_ACTION
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val pendingIntentCancel2 = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java),
            PendingIntent.FLAG_CANCEL_CURRENT
        )
        val pendingIntentCancel3 = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = START_PHONE_SERVICES
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        if (!wasInBackground) {
            Log.d(TAG, "createAlertNotification: background Yes")
            val notification = NotificationCompat.Builder(this, CHANNEL_ALERT_SYSTEM_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Motion Detected")
                .setContentText("After 30 Second the service will make calls")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                //.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .addAction(R.color.RED, "Cancel", pendingIntentCancel)
                .setFullScreenIntent(pendingIntentCancel2, true)
                .setDeleteIntent(pendingIntentCancel3)
                .setWhen(System.currentTimeMillis())
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setUsesChronometer(true)
                .setSound(
                    Uri.parse(
                        "android.resource://"
                                + packageName + "/" + R.raw.siren
                    )
                )
                .setTimeoutAfter(30000)
                .build()
            notification.flags = notification.flags or Notification.FLAG_INSISTENT
            notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
            notification.visibility = Notification.VISIBILITY_PUBLIC
            notificationManager.notify(MOTION_ALERT_SYSTEM_NOTIFICATION_ID, notification)
        } else {
            Log.d(TAG, "createAlertNotification: background No")
            val notification = NotificationCompat.Builder(this, CHANNEL_ALERT2_SYSTEM_ID)
                .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
                .setContentTitle("Motion Detected")
                .setContentText("After 30 Second the service will make calls")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                //.setCategory(NotificationCompat.CATEGORY_PROGRESS)
                .addAction(R.color.RED, "Cancel", pendingIntentCancel)
                .setFullScreenIntent(pendingIntentCancel2, true)
                .setDeleteIntent(pendingIntentCancel3)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis())
                .setUsesChronometer(true)
                .setSound(
                    Uri.parse(
                        "android.resource://"
                                + packageName + "/" + R.raw.siren
                    )
                )
                .setTimeoutAfter(30000)
                .build()


            // notification.flags= Notification.FLAG_ONGOING_EVENT
            notification.flags = notification.flags or Notification.FLAG_INSISTENT
            notification.flags = notification.flags or Notification.FLAG_NO_CLEAR
            notification.flags = notification.flags or Notification.FLAG_ONGOING_EVENT
            notification.visibility = Notification.VISIBILITY_PUBLIC
            notificationManager.notify(MOTION_ALERT_SYSTEM_NOTIFICATION_ID2, notification)
        }


    }


    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onMoveToForeground() {
        // app moved to foreground
        //  Log.d(TAG, "onMoveToForeground: in forground")
        wasInBackground = true
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onMoveToBackground() {
        // app moved to background
        // Log.d(TAG, "onMoveToBackground: in background")
        wasInBackground = false
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        mediaSession!!.isActive = false

        super.onDestroy()

    }





}