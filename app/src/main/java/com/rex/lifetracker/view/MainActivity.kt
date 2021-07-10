package com.rex.lifetracker.view

import android.Manifest
import android.animation.ObjectAnimator
import android.app.Dialog
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.GravityCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.adapter.Contacts_RecyclerView
import com.rex.lifetracker.databinding.ActivityMainBinding
import com.rex.lifetracker.service.MotionDetectService
import com.rex.lifetracker.service.UIChange
import com.rex.lifetracker.utils.Constant
import com.rex.lifetracker.utils.Constant.ACTION_START_SERVICE
import com.rex.lifetracker.utils.Constant.ACTION_STOP_SERVICE
import com.rex.lifetracker.utils.Constant.MOTION_ALERT_SYSTEM_NOTIFICATION_ID
import com.rex.lifetracker.utils.Constant.MOTION_ALERT_SYSTEM_NOTIFICATION_ID2
import com.rex.lifetracker.utils.Constant.REQUESTED_PERMISSION_CODE
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {
    private lateinit var binding: ActivityMainBinding
    private lateinit var startDateValue: Date
    private val simpleDateFormat: SimpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    private lateinit var endDateValue: Date
    private var appsState = false
    private var sosActivityState = false
    private val calendar = Calendar.getInstance()
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController
    private lateinit var mAdapter: Contacts_RecyclerView
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private lateinit var userActiveTime: String
    private var isInternetConnected = false
    private var internetDisposable: Disposable? = null
    private var timeCountInMilliSeconds = (30 * 1000).toLong()
    private var countDownTimer: CountDownTimer? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        requestPermission()
        initViewModel()
        initValue()
        setViewValue()
        setObservers()


//------------------------------------binding----------------------------------------//
        binding.apply {

            //controlling bottom nav
            bottomNavigationView.setOnItemSelectedListener { menu ->
                when (menu.itemId) {
                    R.id.notification -> {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_HIDDEN
                        return@setOnItemSelectedListener NavigationUI.onNavDestinationSelected(
                            menu,
                            navController
                        )
                    }
                    R.id.mapsFragment -> {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_COLLAPSED
                        return@setOnItemSelectedListener NavigationUI.onNavDestinationSelected(
                            menu,
                            navController
                        )
                    }
                    else -> {
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_HIDDEN
                        return@setOnItemSelectedListener NavigationUI.onNavDestinationSelected(
                            menu,
                            navController
                        )
                    }
                }
            }

            //warning bottom sheet
            stopServicesBeforeCall.setOnClickListener {

//                bottomSheet.visibility = View.VISIBLE
                BottomSheetBehavior.from(bottomSheet2).state = BottomSheetBehavior.STATE_COLLAPSED
                bottomSheet2.visibility = View.GONE
                BottomSheetBehavior.from(bottomSheet).state = BottomSheetBehavior.STATE_EXPANDED
//                bottomSheet2.visibility = View.GONE
                val notificationManager =
                    getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.cancel(MOTION_ALERT_SYSTEM_NOTIFICATION_ID)
                notificationManager.cancel(MOTION_ALERT_SYSTEM_NOTIFICATION_ID2)
                serviceStart()

                // stopService(Intent(this@MainActivity2, MotionDetectService::class.java))
            }

            //controlling nav drawer
            drawerIndicator.setOnClickListener {
                drawerlayout.openDrawer(GravityCompat.START)
            }

            //sim selection
            simSelection.setOnClickListener {
                drawerlayout.closeDrawer(GravityCompat.START)
                selectSIMDialog()
            }
            //server start button
            AppTurnOn.setOnClickListener {
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }

                sosActivityState = false
                requestPermission()
            }

            //server stop button
            turnOffServices.setOnClickListener {
                BottomSheetBehavior.from(bottomSheet).apply {
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }

                Toast.makeText(
                    this@MainActivity,
                    "services stop",
                    Toast.LENGTH_SHORT
                )
                    .show()

                stopService(
                    Intent(
                        this@MainActivity,
                        MotionDetectService::class.java
                    ).apply {
                        this.action = ACTION_STOP_SERVICE
                    })


            }

            //adding more trusted contacts
            addMoreContacts.setOnClickListener {
                stopService(
                    Intent(
                        this@MainActivity,
                        MotionDetectService::class.java
                    )
                )
                finish()
                startActivity(
                    Intent(
                        this@MainActivity,
                        TrustedNumberDetails::class.java
                    )
                )
                finish()
            }

            //loging out button
            navLogOut.setOnClickListener {

                Firebase.auth.signOut()
                googleSignInClient.signOut()
                stopService(
                    Intent(
                        this@MainActivity,
                        MotionDetectService::class.java
                    )
                )
                finish()
                startActivity(Intent(this@MainActivity, SignIn::class.java).putExtra("Nuke", "YES"))
                finish()


            }
            navAddContacts.setOnClickListener {

                stopService(
                    Intent(
                        this@MainActivity,
                        MotionDetectService::class.java
                    )
                )
                finish()
                startActivity(
                    Intent(
                        this@MainActivity,
                        TrustedNumberDetails::class.java
                    )
                )
                finish()

            }
        }


    }


    private fun initValue() {
        mAdapter = Contacts_RecyclerView(this)
        userActiveTime = simpleDateFormat.format(calendar.time)

        binding.apply {

            //bottomNavigationView.setupWithNavController(navController)
            setSupportActionBar(upperNav)
            navHostFragment =
                supportFragmentManager.findFragmentById(fragmentContainerView.id) as NavHostFragment
            navController = navHostFragment.navController
            NavigationUI.setupWithNavController(bottomNavigationView, navController)

            //recycler setup
            recyclerViewSheet.apply {
                adapter = mAdapter
                layoutManager = LinearLayoutManager(
                    this@MainActivity,
                    LinearLayoutManager.HORIZONTAL,
                    false
                )
                setHasFixedSize(true)
            }

            //setting up bottom sheet peek height
            BottomSheetBehavior.from(bottomSheet).apply {
                val tv = TypedValue()
                if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        resources.displayMetrics
                    )
                    peekHeight = actionBarHeight / 2
                }

                state = BottomSheetBehavior.STATE_COLLAPSED
            }

            //warning bottom sheet behavior
            BottomSheetBehavior.from(bottomSheet2).apply {
                val tv = TypedValue()
                if (theme.resolveAttribute(R.attr.actionBarSize, tv, true)) {
                    val actionBarHeight = TypedValue.complexToDimensionPixelSize(
                        tv.data,
                        resources.displayMetrics
                    )
                    peekHeight = actionBarHeight / 2
                }

                state = BottomSheetBehavior.STATE_COLLAPSED
            }


        }

    }


    private fun initViewModel() {
        //local database
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom)
                .setCancelable(true).build()
        dialogue?.show()
        localDataBaseViewModel.realAllUserInfo.observe(this, {
            if (it.isNotEmpty()) {
                dialogue.dismiss()
                trailCalculation(userActiveTime, it[0].Deactivate_Time)
            }


        })


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)


    }




    private fun setViewValue() {

        binding.apply {


            //setting trusted Contacts
            localDataBaseViewModel.readAllContacts.observe(
                this@MainActivity,
                { contacts ->

                    if (contacts.isNotEmpty()) {
                        //setting user information
                        localDataBaseViewModel.realAllUserInfo.observe(
                            this@MainActivity,
                            { userInfo ->
                                if (userInfo.isNotEmpty()) {
                                    mAdapter.setData(contacts)

                                    if (userInfo[0].Image != null) {
                                        Glide.with(this@MainActivity)
                                            .asBitmap()
                                            .load(userInfo[0].Image)
                                            .placeholder(R.drawable.ic_man)
                                            .into(navUserImage)
                                    }
                                    navuserGmail.text = userInfo[0].User_Email

                                }
                            })
                    }
                })


        }

    }

    private fun selectSIMDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(false)
        dialog.setContentView(R.layout.sim_select_dialog)
        dialog.window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.WHITE))
        val cancel = dialog.findViewById<Button>(R.id.cancelDialog)
        val ok = dialog.findViewById<Button>(R.id.okDialog)
        val sim1Check = dialog.findViewById<RadioButton>(R.id.sim1)
        val sim2Check = dialog.findViewById<RadioButton>(R.id.sim2)

        localDataBaseViewModel.readAllSIMSlot.observe(this, { databaseSIM ->

            if (databaseSIM.isNotEmpty()) {
                if (databaseSIM[0].SELECTED_SIM_SLOT == "0") {
                    sim2Check.isChecked = false
                    sim1Check.isChecked = true
                } else {
                    sim1Check.isChecked = false
                    sim2Check.isChecked = true
                }
            } else {
                sim2Check.isChecked = false
                sim1Check.isChecked = false
                Log.d(TAG, "selectSIMDialog: No Data")
            }

            sim1Check.setOnClickListener {
                sim2Check.isChecked = false
                sim1Check.isChecked = true
            }

            sim2Check.setOnClickListener {
                sim1Check.isChecked = false
                sim2Check.isChecked = true
            }

            ok.setOnClickListener {
                if (!sim1Check.isChecked && !sim2Check.isChecked) {

                    Toast.makeText(
                        this,
                        "Default SIM will make your Calls ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (sim1Check.isChecked) {
                    localDataBaseViewModel.addSIMSlot(
                        SIM_Entity(
                            0, "0"
                        )
                    )

                } else if (sim2Check.isChecked) {
                    localDataBaseViewModel.addSIMSlot(
                        SIM_Entity(
                            0, "1"
                        )
                    )

                }
                dialog.dismiss()
            }

            cancel.setOnClickListener {
                Toast.makeText(this, "Default SIM will make your Calls ", Toast.LENGTH_SHORT)
                    .show()
                dialog.dismiss()
            }

        })

        dialog.show()

    }


    private fun trailCalculation(userActiveTime: String, dueDate: String) {
        startDateValue = simpleDateFormat.parse(userActiveTime)
        endDateValue = simpleDateFormat.parse(dueDate)

        val remain = (TimeUnit.DAYS.convert(
            (endDateValue.time - startDateValue.time),
            TimeUnit.MILLISECONDS
        )).toString()

        Log.d(TAG, "initViewModel: Time: " + Integer.parseInt(remain))

        binding.apply {
            trailProgress.max = 100
            ObjectAnimator.ofInt(trailProgress, "Progress", 15 * (7 - Integer.parseInt(remain)))
                .setDuration(2000)
                .start()
            remainingDays.text = (Integer.parseInt(remain)).toString()

        }

    }

    private fun serviceStart() {
        //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show()
        startService(Intent(this, MotionDetectService::class.java).apply {
            this.action = ACTION_START_SERVICE
        })
        Log.d(TAG, "serviceStart: Started Services")
    }

    private fun setObservers() {
        MotionDetectService.uiChange.observe(this, {
            when (it) {
                is UIChange.START -> {
                    binding.apply {
                        start()
                        BottomSheetBehavior.from(bottomSheet).state =
                            BottomSheetBehavior.STATE_COLLAPSED
                        bottomSheet2.visibility = View.VISIBLE
                        BottomSheetBehavior.from(bottomSheet2).state =
                            BottomSheetBehavior.STATE_EXPANDED
                    }
                }
                is UIChange.END -> {
                    //Toast.makeText(this, "Working out", Toast.LENGTH_SHORT).show()
                    binding.apply {
//                        BottomSheetBehavior.from(bottomSheet).state =
//                            BottomSheetBehavior.STATE_EXPANDED
                        bottomSheet.visibility = View.VISIBLE
                        bottomSheet2.visibility = View.GONE
                        progressBarCounter.clearAnimation()
                        countDownTimer?.cancel()


//                        BottomSheetBehavior.from(bottomSheet2).state = BottomSheetBehavior.STATE_HIDDEN
                        bottomSheet2.visibility = View.GONE
                        //bottomSheet.background =ContextCompat.getDrawable(this@MainActivity2,R.drawable.shape_colorgradient_all_activities)
                    }
                }
            }
        })


    }

    private fun start() {
        setTimerValues()
        startCountDownTimer()
    }

    private fun setTimerValues() {
        // assigning values after converting to milliseconds
        timeCountInMilliSeconds = (30 * 1000).toLong()
        setProgressBarValues()
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(timeCountInMilliSeconds, 50) {
            override fun onTick(millisUntilFinished: Long) {
                binding.progressBarCounter.progress = 600 - (millisUntilFinished / 50).toInt()
            }

            override fun onFinish() {
                binding.apply {
                    BottomSheetBehavior.from(bottomSheet2).state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheet2.visibility = View.GONE
                    BottomSheetBehavior.from(bottomSheet2).state = BottomSheetBehavior.STATE_COLLAPSED
                    bottomSheet.visibility = View.VISIBLE
                }
                setProgressBarValues() // call to initialize the progress bar values
            }
        }.start()
    }

    private fun setProgressBarValues() {
        binding.progressBarCounter.max = timeCountInMilliSeconds.toInt() / 50
        binding.progressBarCounter.progress = timeCountInMilliSeconds.toInt() / 1000
    }


    //
//------------------------------------------------------------------------permission sector ------------------------------------------------------------
    private fun requestPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without  Permission.",
            REQUESTED_PERMISSION_CODE,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    }

    private fun hasPermission() =
        EasyPermissions.hasPermissions(
            this,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.SEND_SMS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.FOREGROUND_SERVICE
        )


    private fun userDialog() {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Permission!")
            .setMessage("Need Display Over Apps Permission")
            .setIcon(R.drawable.ic_baseline_error_24)
            .setPositiveButton("Yes") { _, _ ->
                screenOverLapRequest()
                Log.d(TAG, "userDialog: is called in yes button")
            }
            .setNegativeButton("NO") { _, _ ->
                Toast.makeText(
                    this,
                    "This Application won't Work properly without Display Over Apps Permission",
                    Toast.LENGTH_SHORT
                ).show()
                Handler(Looper.getMainLooper()).postDelayed({
                    stopService(Intent(this@MainActivity, MotionDetectService::class.java))
                    finishAndRemoveTask()

                }, 2000)
            }.create()

        dialog.show()
    }

    private fun screenOverLapRequest() {
        Log.d(TAG, "screenOverLapRequest: is called")
        startActivity(
            Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + this.packageName)
            )
        )
    }


    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(this).build().show()
        } else {
            requestPermission()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        if (!hasPermission()) {
            // requestLocationPermission()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
                !Settings.canDrawOverlays(this)
            ) {
                userDialog()
                Log.d(TAG, "onPermissionsGranted: dialog")

            } else {

                if (!sosActivityState) {
                    serviceStart()
                    appsState = true
                }

            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: is called")

        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                isInternetConnected = isConnectedToInternet
            }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
            Settings.canDrawOverlays(this)
        ) {
            if (!appsState) {
                requestPermission()
            }

        }

        Log.d(TAG, "onPostResume: is called")
    }

    override fun onPause() {
        super.onPause()
        safelyDispose(internetDisposable)
    }

    private fun safelyDispose(disposable: Disposable?) {
        if (disposable != null && !disposable.isDisposed) {
            disposable.dispose()
        }
    }

    //--------NetWork-----------------//


    override fun onBackPressed() {
        binding.apply {
            bottomSheet.visibility = View.VISIBLE

        }
        super.onBackPressed()
    }


}

