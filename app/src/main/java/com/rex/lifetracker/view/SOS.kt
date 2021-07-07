package com.rex.lifetracker.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.CallLog
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.telephony.PhoneStateListener
import android.telephony.SmsManager
import android.telephony.SubscriptionManager
import android.telephony.TelephonyManager
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.rex.lifetracker.databinding.ActivitySosBinding
import com.rex.lifetracker.utils.Constant.REQUEST_PERMISSION
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import java.text.SimpleDateFormat
import java.util.*


class SOS : AppCompatActivity() {
    private var count = 0
    private var count2 = 0
    private var calling = false
    private var callingFirst = true
    private lateinit var telephonyManager: TelephonyManager
    private lateinit var smsManager: SmsManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivitySosBinding
    private lateinit var SOSNumber2: String
    private lateinit var sms: String
    private val messagingNumber = ArrayList<String>()
    private val callingNumber = ArrayList<String>()
    private var duration: Int = 0
    private var index = 0
    private var indexFlag = false
    private var simSlotFlag = false
    private var simSlotNumber = 0
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel


    private var mPhoneStateListener: PhoneStateListener = object : PhoneStateListener() {

        override fun onCallStateChanged(state: Int, number: String) {

            Log.d(TAG, "onCallStateChanged: is called ")
            when (state) {

                TelephonyManager.CALL_STATE_IDLE -> {
                    if (!calling) {
                        makePhoneCall(callingNumber[count2])
                        calling = true

                    } else if (calling && duration <= 1 && count < callingNumber.size) {
                        callingFirst = false
                        Log.d(TAG, "onCallStateChanged: count $count")
                        count2++

                        if (count2 == 2) {
                            count++
                            count2 = 0
                        }
                        if (count >= callingNumber.size) {
                            unregisterTelephoneListen()
                        } else {
                            makePhoneCall(callingNumber[count])
                        }

                    } else {
                        Log.d(TAG, "onCallStateChanged: unregister is called")
                        count = 0
                        count2 = 0
                        unregisterTelephoneListen()
                    }

                }


            }
            Log.d(TAG, "onCallStateChanged last call duration ---- else: $duration")
            duration = getLastCallDuration()

            // Toast.makeText(this@SOS, "Help", Toast.LENGTH_SHORT).show()

        }
    }

    private fun unregisterTelephoneListen() {
        Log.d(TAG, "unregisterTelephoneListen: is called")
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_NONE)


        startActivity(Intent(this, MainActivity::class.java).putExtra("Service", "YES"))
        finish()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySosBinding.inflate(layoutInflater)
        setContentView(binding.root)


        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (count == 0) {
            initModelView()
            checkSimSlot()
        }


    }

    private fun initModelView() {

        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)

        arrangeArrayList()


    }

    private fun arrangeArrayList() {
        localDataBaseViewModel.readAllContacts.observe(this, {

            var i = 0
            while (i < it.size) {
                when (it[i].Priority) {
                    "First" -> {
                        Log.d(TAG, "initModelView: index $i is first")
                        if (callingNumber.isEmpty()) {
                            callingNumber.add(0, it[i].Phone)
                        } else {
                            val temp = callingNumber[0]
                            callingNumber[0] = it[i].Phone
                            callingNumber.add(i, temp)
                        }

                    }
                    "Second" -> {
                        Log.d(TAG, "initModelView: index $i is second")

                        when {
                            callingNumber.isEmpty() -> {
                                SOSNumber2 = it[i].Phone
                                Log.d(TAG, "initModelView: phone $SOSNumber2")
                                callingNumber.add(i, it[i + 1].Phone)
                                index = i
                                Log.d(TAG, "initModelView: index --- > $index")
                                Log.d(TAG, "initModelView: short array -- > $callingNumber")
                                indexFlag = true

                            }
                            callingNumber.size == 1 -> {
                                callingNumber.add(i, it[i].Phone)
                            }
                            else -> {
                                val temp = callingNumber[1]
                                callingNumber[1] = it[i].Phone
                                callingNumber.add(i, temp)
                            }
                        }


                    }
                    else -> {

                        callingNumber.add(i, it[i].Phone)
                    }
                }
                messagingNumber.add(i, it[i].Phone)

                i++
            }

            if (indexFlag) {
                Log.d(TAG, "initModelView: flag is true")
                callingNumber[index + 1] = SOSNumber2
            }

            Log.d(TAG, "initModelView: list number array $messagingNumber")
            Log.d(TAG, "initModelView: list number array sorted --->  $callingNumber")


            getCurrentLocation()

        })
    }

    private fun checkSimSlot() {

        localDataBaseViewModel.readAllSIMSlot?.observe(this, androidx.lifecycle.Observer {
            smsManager = if (it.isNotEmpty()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    simSlotFlag = true
                    val subs =
                        getSystemService(TELEPHONY_SUBSCRIPTION_SERVICE) as SubscriptionManager

                    val sim = subs.getSubscriptionIds(Integer.parseInt(it[0].SELECTED_SIM_SLOT))
                    if(sim != null){
                        simSlotNumber = Integer.parseInt(it[0].SELECTED_SIM_SLOT)
                        simSlotFlag = true
                        val simm = sim[0]
                        SmsManager.getSmsManagerForSubscriptionId(simm)
                    }else{
                        simSlotFlag = false
                        SmsManager.getDefault()
                    }

                } else {
                    simSlotFlag = false
                    SmsManager.getDefault()
                }
            } else {
                simSlotFlag = false
                SmsManager.getDefault()
            }
        })
    }


    private fun getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_PERMISSION
            )
            return
        } else {

            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        Log.d(TAG, "getCurrentLocation: ${location.latitude},${location.longitude}")
                        sms =
                            "https://maps.google.com/?q=${location.latitude},${location.longitude}"
                        Log.d(TAG, "getCurrentLocation: is $sms")
                        sendSms(sms)


                    }
                }

        }


    }

    private fun sendSms(s: String) {


        var i = 0
        while (i < messagingNumber.size) {
            smsManager.sendTextMessage(messagingNumber[i], null, "Help! -- > $s", null, null)
            i++
        }
        telephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)


    }

    @SuppressLint("MissingPermission")
    private fun makePhoneCall(number: String) {

        if (simSlotFlag) {
            Log.e(TAG, "makePhoneCall: has sim slot")
            var phoneAccountHandleList: List<PhoneAccountHandle?> = emptyList()
            val item = simSlotNumber // 0 for sim1 & 1 for sim2

            val simSlotName = arrayOf(
                "extra_asus_dial_use_dualsim",
                "com.android.phone.extra.slot",
                "slot",
                "simslot",
                "sim_slot",
                "subscription",
                "Subscription",
                "phone",
                "com.android.phone.DialingMode",
                "simSlot",
                "slot_id",
                "simId",
                "simnum",
                "phone_type",
                "slotId",
                "slotIdx"
            )
            val telecomManager =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.getSystemService(TELECOM_SERVICE) as TelecomManager
                } else {
                    TODO("VERSION.SDK_INT < LOLLIPOP")
                }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                phoneAccountHandleList = telecomManager.callCapablePhoneAccounts
            }
            val intent =
                Intent(Intent.ACTION_CALL).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.data = Uri.parse("tel:$number")
            intent.putExtra("c-om.android.phone.force.slot", true)
            intent.putExtra("Cdma_Supp", true)
            if (item === 0) { //for sim1
                for (s in simSlotName) {
                    intent.putExtra(s, 0) //0 or 1 according to sim.......
                }
                if (phoneAccountHandleList.isNotEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        intent.putExtra(
                            "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                            phoneAccountHandleList[0]
                        )
                    }
                }
            } else { //for sim2
                for (s in simSlotName) {
                    intent.putExtra(s, 1) //0 or 1 according to sim.......
                }
                if (phoneAccountHandleList.isNotEmpty()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        intent.putExtra(
                            "android.telecom.extra.PHONE_ACCOUNT_HANDLE",
                            phoneAccountHandleList[1]
                        )
                    }
                }
            }
            startActivity(intent)

        } else {
            Log.e(TAG, "makePhoneCall: default sim slot")
            startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$number")))
            Log.d(TAG, "makePhoneCall: is called")
        }


    }


    private fun getLastCallDuration(): Int {
        var duration: Int
        val cur: Cursor? = contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, CallLog.Calls.DATE + " DESC limit 1;"
        )


        if (cur != null && cur.moveToFirst()) {
            Log.d(
                TAG,
                "getLastCallDuration: is called is --------> " + cur.getString(
                    cur.getColumnIndex(CallLog.Calls.DURATION)
                )
            )
            duration = if (callingFirst) {
                Log.d(TAG, "getLastCallDuration: first")
                0
                //callingFirst = false
            } else {
                Integer.parseInt(cur.getString(cur.getColumnIndex(CallLog.Calls.DURATION)))
            }
        } else {
            //Log.d(TAG, "getLastCallDuration: else block ---- >"+cur!!.getColumnIndex(CallLog.Calls.DURATION))
            duration = 0

        }

        return duration
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                getCurrentLocation()
            } else {
                Toast.makeText(this, "need to access map", Toast.LENGTH_SHORT).show()
                getCurrentLocation()

            }
        }
    }


    override fun onPostResume() {
        //  duration = getLastCallDuration()
//        if (duration > 1) {
//            Log.d(TAG, "onPostResume: is called")
//            unregisterTelephoneListen()
//        }
//        Log.d(
//            TAG,
//            "onPostResume: is called SOS ${SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().time)}"
//        )
        super.onPostResume()
    }

    override fun onPause() {
        //  duration = getLastCallDuration()
//        if (duration > 1){
//            unregisterTelephoneListen()
//        }
        Log.d(
            TAG,
            "onPause: is called after ${SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().time)}"
        )
        super.onPause()
    }

    override fun onStart() {
        Log.d(
            TAG,
            "onStart: is called ${SimpleDateFormat("dd.MM.yyyy hh:mm:ss").format(Calendar.getInstance().time)}"
        )
        super.onStart()
    }


}