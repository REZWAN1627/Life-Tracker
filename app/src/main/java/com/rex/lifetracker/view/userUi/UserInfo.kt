package com.rex.lifetracker.view.userUi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.databinding.ActivityUserInfoBinding
import com.rex.lifetracker.model.FireBaseModel.UserInfoModel
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.TrustedNumberDetails
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SimSlotViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.TrustedContactsViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class UserInfo : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var trustedContactsViewModel: TrustedContactsViewModel
    private var After7DaysDate: String? = null
    private var currentDate: String? = null
    private var currentDate2: String? = null
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    private val simpleDateFormat2 = SimpleDateFormat("EEE, d MMM -HH:mm")
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var globalEmail: String = "null"
    private var globalImage: String = "null"
    private var subsPack: String = "Trail Version"
    private var broughtPack: String = "null"
    private var status: String = "On going"
    private var isInternetConnected = false
    private var internetDisposable: Disposable? = null
    private lateinit var simSlotViewModel: SimSlotViewModel
    private lateinit var startDateValue2: Date
    private lateinit var endDateValue2: Date


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initModel()
        formattingDate()
        setInfo()
        simSlot()
        // populateDataBase()

        // showDialog()

        binding.apply {
            next.setOnClickListener {

                if (TextUtils.isEmpty(userFirstName.text.toString()) || TextUtils.isEmpty(
                        userLastName.text.toString()
                    )
                ) {
                    userFirstName.error = "Empty Field"
                    userLastName.error = "Empty Field"
                    return@setOnClickListener
                } else {

                    if (isInternetConnected) {
                        insertValue(
                            userFirstName.text.toString(),
                            userLastName.text.toString(),

                            )
                        Log.d(TAG, "getInfo: " + userLastName.text)
                    } else {
                        Toast.makeText(
                            this@UserInfo,
                            "You need Internet Connect",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                }

            }
        }


    }

    private fun simSlot() {
        simSlotViewModel.getSelectedSimSlot.observe(this, { sim ->
            if (sim != null) {
                localDataBaseViewModel.addSIMSlot(
                    SIM_Entity(
                        0, sim.SELECTED_SIM_SLOT
                    )
                )
            } else {
                localDataBaseViewModel.addSIMSlot(
                    SIM_Entity(
                        0, "0"
                    )
                )
            }
        })
    }


    private fun formattingDate() {

        currentDate = simpleDateFormat.format(calendar.time)
        currentDate2 = simpleDateFormat2.format(calendar.time)
        broughtPack = currentDate2 as String
        calendar.add(Calendar.DATE, 30) // number of days to add

    }

    private fun initModel() {
        trustedContactsViewModel = ViewModelProvider(this).get(TrustedContactsViewModel::class.java)
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
        Log.d(TAG, "initModel: is called")
        userInfoViewModel = ViewModelProvider(
            this
        ).get(
            UserInfoViewModel::class.java
        )
        signInViewModel = ViewModelProvider(
            this
        ).get(
            SignInViewModel::class.java
        )
        simSlotViewModel = ViewModelProvider(this).get(SimSlotViewModel::class.java)

    }


    private fun setInfo() {

        Log.d(TAG, "setInfo: is called")


        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.LoadingUserInfo)
                .setCancelable(true).build()
        dialogue?.show()

        signInViewModel.collectUserInfoLiveData.observe(this, {
            globalEmail = it.email
            userInfoViewModel.getUserInfoLiveData.observe(this, { user ->
                if (user != null) {
                    Log.d(TAG, "setInfo: all user info $user")
                    dialogue?.dismiss()
                    binding.apply {
                        userFirstName.setText(user.first_Name)
                        userLastName.setText(user.last_Name)
                        globalImage = user.avatar_image
                        subsPack = user.subscription_pack.toString()
                        After7DaysDate = user.deactivate_Time
                        broughtPack = user.brought_pack_time.toString()
                        val time = trailCalculation(user.deactivate_Time)
                        status = if (time == 0){
                            "END"
                        }else{
                            user.status.toString()
                        }
                        currentDate = user.active_Time
                        userGmail.text = it.email
                        Glide.with(this@UserInfo).load(globalImage)
                            .centerCrop()
                            .placeholder(R.drawable.ic_team)
                            .into(UserImage)


                    }
                } else {
                    dialogue?.dismiss()
                    After7DaysDate = simpleDateFormat.format(calendar.time)
                    globalImage = it.imageUrl
                    globalEmail = it.email
                    binding.apply {
                        userGmail.text = it.email
                        Glide.with(this@UserInfo).load(it.imageUrl)
                            .centerCrop()
                            .placeholder(R.drawable.ic_team)
                            .into(UserImage)

                    }
                }
            })
        })


    }

    private fun insertValue(
        firstName: String,
        lastName: String
    ) {
        Log.d(TAG, "insertValue: is called")

        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.LoadingUserInfo)
                .setCancelable(true).build()
        dialogue?.show()
        val job = CoroutineScope(Dispatchers.IO).launch {
            val result =
                localDataBaseUpload(getBitmap(globalImage), firstName, lastName)
            val result2 = FirebaseUpload(getByte(getBitmap(globalImage)), firstName, lastName)

        }
        job.invokeOnCompletion {
            dialogue.dismiss()
            startActivity(Intent(this@UserInfo, TrustedNumberDetails::class.java))
            finish()
        }


    }

    private fun FirebaseUpload(image: ByteArray, firstName: String, lastName: String) {
        userInfoViewModel.insert(
            UserInfoModel(
                currentDate.toString(), "", broughtPack,
                After7DaysDate.toString(), firstName, lastName, status, subsPack
            ),
            image
        )

    }

    private fun localDataBaseUpload(bitmap: Bitmap, firstName: String, lastName: String) {
        Log.d(TAG, "localDataBaseUpload: is caled")
        localDataBaseViewModel.addUserInfo(
            PersonalInfo_Entity(
                0,
                firstName,
                lastName,
                After7DaysDate!!,
                currentDate!!,
                subsPack,
                broughtPack,
                status,
                globalEmail,
                bitmap
            )
        )

    }

    private suspend fun getBitmap(imageUri: String): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUri)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }

    private fun getByte(image: Bitmap): ByteArray {
        Log.d(TAG, "getByte: is called")
        val baos = ByteArrayOutputStream()
        val bitmap = image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()

    }

    override fun onResume() {
        super.onResume()
        internetDisposable = ReactiveNetwork
            .observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                isInternetConnected = isConnectedToInternet

            }
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


    private fun trailCalculation(deactivatedTime: String): Int {
        startDateValue2 = simpleDateFormat.parse(simpleDateFormat.format(Calendar.getInstance().time))
        endDateValue2 = simpleDateFormat.parse(deactivatedTime)
        val remain = (TimeUnit.DAYS.convert(
            (endDateValue2.time - startDateValue2.time),
            TimeUnit.MILLISECONDS
        )).toString()
        Log.e(TAG, "Trail Calculation: " + Integer.parseInt(remain))
        return Integer.parseInt(remain)


    }


}