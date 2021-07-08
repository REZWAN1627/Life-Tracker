package com.rex.lifetracker.service

import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.RingtoneManager
import android.net.Uri
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rex.lifetracker.R
import com.rex.lifetracker.service.broadcast_receiver.SystemShakeAlert_broadcastReceiver
import com.rex.lifetracker.utils.Constant.ACTIVITY_REQUEST_CODE
import com.rex.lifetracker.utils.Constant.BROADCAST_REQUEST_CODE
import com.rex.lifetracker.utils.Constant.CANCEL_ACTION
import com.rex.lifetracker.utils.Constant.CHANNEL_ALERT_SYSTEM_ID
import com.rex.lifetracker.utils.Constant.CHANNEL_ID
import com.rex.lifetracker.utils.Constant.FOREGROUND_NOTIFICATION_ID
import com.rex.lifetracker.utils.Constant.MOTION_ALERT_SYSTEM_NOTIFICATION_ID
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.MainActivity
import com.rex.lifetracker.view.SOS
import kotlin.math.roundToInt
import kotlin.math.sqrt


class MotionDetectService : Service(), SensorEventListener {

    //    private var xAxis = 0f
//    private var yAxis = 0f
//    private var zAxis = 0f
//    private var previousXAXIS = 0f
//    private var previousYAXIS = 0f
//    private var previousZAXIS = 0f
//    private var estimatedShakeValue = 12.5f
//    private var firstUpdate = true
//    private var shakeState = false
    private var sensorManager: SensorManager? = null
    private var sensor: Sensor? = null
    private lateinit var notificationManager: NotificationManagerCompat

//    private val mSensorX = 0f
//    private val mSensorY = 0f
//    private val mDisplay: Display? = null
//    private val mPowerManager: PowerManager? = null
//    private val mWindowManager: WindowManager? = null
//
//    var gravity = DoubleArray(3)
//    var linear_acceleration = DoubleArray(3)

    private var accelerationX = 0.0
    private var accelerationY: Double = 0.0
    private var accelerationZ: Double = 0.0

    //accident threashold
    private val threshold = 50

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


    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        notificationManager = NotificationManagerCompat.from(this)
    }

    override fun onDestroy() {
        sensorManager?.unregisterListener(this)
        stopSelf()
        super.onDestroy()

    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        sensorManager?.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("Service", "YES")
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        )


        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Life Tracker is Activated")
            .setContentText("Drive Safe, Keep your eyes on the road")
            .setSmallIcon(R.drawable.ic_baseline_directions_bike_24)
            .addAction(R.color.RED, "Stop Life Tracking", pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
            // .setContentIntent(pendingIntent)
            .build()
        startForeground(FOREGROUND_NOTIFICATION_ID, notification)
        // createAlertNotification()

        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent?) {


        accelerationX = (event!!.values[0] * 1000).roundToInt() / 1000.0
        Log.d(TAG, "onSensorChanged: acceleration X -------> $accelerationX")
        accelerationY = (event!!.values[1] * 1000).roundToInt() / 1000.0
        Log.d(TAG, "onSensorChanged: acceleration Y -------> $accelerationY")
        accelerationZ = (event!!.values[2] * 1000).roundToInt() / 1000.0
        Log.d(TAG, "onSensorChanged: acceleration Z -------> $accelerationZ")

        Log.d(TAG, "===============================================================")
        Log.d(TAG, "===============================================================")

        /* Detect Accident */
        if (accelerationX > threshold || accelerationY > threshold || accelerationZ > threshold) {
            Log.d(TAG, "threshold == $threshold")
            Log.d(TAG, "final: acceleration X -------> $accelerationX")
            Log.d(TAG, "final: acceleration Y -------> $accelerationY")
            Log.d(TAG, "final: acceleration Z -------> $accelerationZ")
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


//version 1.0------------------------------------------------------
        /* // alpha is calculated as t / (t + dT)
         // with t, the low-pass filter's time-constant
         // and dT, the event delivery rate
         val alpha = 0.8.toFloat()

         gravity[0] = alpha * gravity[0] + (1 - alpha) * event!!.values[0]
         Log.d(TAG, "onSensorChanged: gravity[0] ---> " + gravity[0])
         gravity[1] = alpha * gravity[1] + (1 - alpha) * event!!.values[1]
         Log.d(TAG, "onSensorChanged: gravity[1] ---> " + gravity[1])
         gravity[2] = alpha * gravity[2] + (1 - alpha) * event!!.values[2]
         Log.d(TAG, "onSensorChanged: gravity[2] ---> " + gravity[2])

         Log.d(TAG, "-------------------------------------------------")
         Log.d(TAG, "=================================================")

         linear_acceleration[0] = event!!.values[0] - gravity[0]
         Log.d(
             TAG,
             "onSensorChanged: linear_accleration [0] ---> " + linear_acceleration[0]
         )
         linear_acceleration[1] = event!!.values[1] - gravity[1]
         Log.d(
             TAG,
             "onSensorChanged: linear_accleration [1] ---> " + linear_acceleration[1]
         )
         linear_acceleration[2] = event!!.values[2] - gravity[2]
         Log.d(
            TAG,
             "onSensorChanged: linear_accleration [2] ---> " + linear_acceleration[2]
         )

         Log.d(TAG, "-------------------------------------------------")
         Log.d(TAG, "=================================================")

         val total = sqrt(
             linear_acceleration[0] *
                     linear_acceleration[0] + (linear_acceleration[1] *
                     linear_acceleration[1]) + ((linear_acceleration[2]
                     * linear_acceleration[2]))
         ) / 9.8
         Log.d(TAG, "onSensorChanged: total math ---> $total")
         val t = "" + total
         Log.d(TAG, "onSensorChanged: -----------> $t")
         if (total > 2.5) {
             executeShakeAction()
         }*/

//old version//
        /*if (event != null) {
//            Log.d(
//                TAG,
//                "onSensorChanged: X-> " + event.values[0] + " Y -> " + event.values[1] + " Z-> " + event.values[1]
//            )


            updateAccelerometer(event.values[0], event.values[1], event.values[1])
            if (!shakeState && isAccelerometerChanged()) {
                //  Log.d(TAG, "onSensorChanged: is called once")
                shakeState = true
            } else if (shakeState && isAccelerometerChanged()) {
                //  Log.d(TAG, "onSensorChanged: is called twice")
                executeShakeAction()
            } else if (shakeState && !isAccelerometerChanged()) {
                //  Log.d(TAG, "onSensorChanged: is called trice")
                shakeState = false

            }

        }*/

    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    //deprecated
    /* private fun updateAccelerometer(value: Float, value1: Float, value2: Float) {
         // Log.d(TAG, "updateAccelerometer: is called")
         if (firstUpdate) {
             //    Log.d(TAG, "updateAccelerometer: first update")
             previousXAXIS = value
             previousYAXIS = value1
             previousZAXIS = value2
             firstUpdate = false
         } else {
             //   Log.d(TAG, "updateAccelerometer: without first update")
             previousXAXIS = xAxis
             previousYAXIS = yAxis
             previousZAXIS = zAxis
         }
         //  Log.d(TAG, "updateAccelerometer: outside if else")
         xAxis = value
         yAxis = value1
         zAxis = value2
 //        Log.d(TAG, "updateAccelerometer: of x -> $xAxis of y -> $yAxis of z -> $zAxis")
 //        Log.d(
 //            TAG,
 //            "updateAccelerometer: of XX -> $previousXAXIS of YY -> $previousYAXIS of ZZ -> $previousZAXIS"
 //        )
     }

     private fun isAccelerometerChanged(): Boolean {
 //        Log.d(TAG, "isAccelerometerChanged: is called")
 //        Log.d(TAG, "isAccelerometerChanged: of x -> $xAxis of y -> $yAxis of z -> $zAxis")
 //        Log.d(
 //            TAG,
 //            "isAccelerometerChanged: of XX -> $previousXAXIS of YY -> $previousYAXIS of ZZ -> $previousZAXIS"
 //        )
         //change is at-least 2 axis
         val deltaX: Float = abs(previousXAXIS - xAxis)
         val deltaY: Float = abs(previousYAXIS - yAxis)
         val deltaZ: Float = abs(previousZAXIS - zAxis)
         //  Log.d(TAG, "isAccelerometerChanged: X -> $deltaX Y -> $deltaY Z -> $deltaZ")
         return deltaX > estimatedShakeValue && deltaY > estimatedShakeValue || deltaX > estimatedShakeValue && deltaZ > estimatedShakeValue || deltaY > estimatedShakeValue && deltaZ > estimatedShakeValue
     }*/


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
         */Log.d(TAG, "===============================================================")
        Log.d(TAG, "===============================================================")
        Current =
            sqrt(
                (mLinearAcceleration[X] * mLinearAcceleration[X] + mLinearAcceleration[Y] *
                        mLinearAcceleration[Y] + mLinearAcceleration[Z] * mLinearAcceleration[Z]).toDouble()
            ).roundToInt()
                .toFloat()
        Log.d(TAG, "setCurrentAcceleration: current ---- $Current")
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

        Log.d(TAG, "executeShakeAction: working")

        createAlertNotification()

        sensorManager?.unregisterListener(this)
        Toast.makeText(this, "services off", Toast.LENGTH_SHORT).show()
        stopSelf()


    }


    private fun createAlertNotification() {
        Log.d(TAG, "createAlertNotification: is called")
        val pendingIntentCancel = PendingIntent.getBroadcast(
            this,
            BROADCAST_REQUEST_CODE,
            Intent(this, SystemShakeAlert_broadcastReceiver::class.java).also {
                it.action = CANCEL_ACTION
            },
            PendingIntent.FLAG_CANCEL_CURRENT
        )

        val pendingIntentSOS = PendingIntent.getActivity(

            this,
            ACTIVITY_REQUEST_CODE,
            Intent(this, SOS::class.java),
            0

        )


        val notification = NotificationCompat.Builder(this, CHANNEL_ALERT_SYSTEM_ID)
            .setSmallIcon(R.drawable.ic_baseline_add_alert_24)
            .setContentTitle("Motion Detected")
            .setContentText("After 30 Second the service will make calls")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            //.setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .addAction(R.color.RED, "Cancel", pendingIntentCancel)
            .setDeleteIntent(pendingIntentSOS)
            .setWhen(System.currentTimeMillis())
            .setUsesChronometer(true)
            .setVibrate(
                longArrayOf(
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000,
                    1000
                )
            )
            .setShowWhen(true)
            .setSound(
                Uri.parse(
                    "android.resource://"
                            + packageName + "/" + R.raw.siren
                )
            )

            //.setAutoCancel(true)
            //  .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setTimeoutAfter(30000)
            .build()
        notificationManager.notify(MOTION_ALERT_SYSTEM_NOTIFICATION_ID, notification)
    }
}