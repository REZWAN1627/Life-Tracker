package com.rex.lifetracker.view.userUi

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.databinding.ActivityUserInfoBinding
import com.rex.lifetracker.model.FireBaseModel.UserInfoModel
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.TrustedNumberDetails
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel
import dmax.dialog.SpotsDialog
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*


class UserInfo : AppCompatActivity() {
    private lateinit var binding: ActivityUserInfoBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var After7DaysDate: String
    private lateinit var currentDate: String
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    private val firebaseAuth = FirebaseAuth.getInstance()
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var globalEmail = ""
    private var globalImage: String = "null"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        initModel()
        formattingDate()
        setInfo()

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


                    insertValue(
                        userFirstName.text.toString(),
                        userLastName.text.toString(),

                        )
                    Log.d(TAG, "getInfo: " + userLastName.text)
                }

            }
        }


    }

    private fun formattingDate() {

        currentDate = simpleDateFormat.format(calendar.time)
        calendar.add(Calendar.DATE, 7) // number of days to add

    }

    private fun initModel() {
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
        Log.d(TAG, "initModel: is called")
        userInfoViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(
            UserInfoViewModel::class.java
        )
        signInViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(
            SignInViewModel::class.java
        )

        signInViewModel.collectUserData()


    }


    private fun setInfo() {

        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.LoadingUserInfo)
                .setCancelable(true).build()
        dialogue?.show()

        signInViewModel.collectUserInfoLiveData?.observe(this, Observer { it ->

            Log.d(TAG, "setInfo: is called " + it.email)
            globalEmail = it.email
            globalImage = it.imageUrl

                Log.d(TAG, "setInfo: is called")
                userInfoViewModel.getUserInfoLiveData?.observe(
                    this@UserInfo,
                    Observer { userInfo ->
                        dialogue?.dismiss()

                        Log.d(TAG, "setInfo: inside is called $userInfo")

                        if (userInfo != null) {
                            After7DaysDate = userInfo.deactivate_Time
                            binding.apply {

                                if (userInfo.first_Name == "null"
                                    && userInfo.last_Name == "null"
                                    && userInfo.deactivate_Time == "null" && userInfo.active_Time == "null"
                                ) {
                                    After7DaysDate = simpleDateFormat.format(calendar.time)
                                } else if (userInfo.first_Name == "null" ||
                                    userInfo.last_Name == "null" ||
                                    userInfo.deactivate_Time == "null" || userInfo.active_Time == "null"
                                ) {
                                    After7DaysDate = simpleDateFormat.format(calendar.time)

                                } else {
                                    userFirstName.setText(userInfo.first_Name)
                                    userLastName.setText(userInfo.last_Name)
                                    After7DaysDate = userInfo.deactivate_Time
                                    currentDate = userInfo.active_Time
                                }

                            }
                        }else{
                            After7DaysDate = simpleDateFormat.format(calendar.time)
                        }


                    })



            binding.apply {

                userGmail.text = it.email
                Glide.with(this@UserInfo).load(it.imageUrl)
                    .centerCrop()
                    .placeholder(R.drawable.ic_team)
                    .into(UserImage)
                //@tools:sample/avatars
            }

        })
    }

    private fun insertValue(
        firstName: String,
        lastName: String
    ) {

        userInfoViewModel.insert(
            UserInfoModel(
                currentDate,After7DaysDate,firstName,lastName
            )
        )

            if (globalImage != "null") {
                lifecycleScope.launch {
                    localDataBaseViewModel.addUserInfo(
                        PersonalInfo_Entity(
                            0,
                            firstName,
                            lastName,
                            After7DaysDate,
                            currentDate,
                            globalEmail,
                            getBitmap(globalImage)
                        )
                    )
                }


            } else {
                localDataBaseViewModel.addUserInfo(
                    PersonalInfo_Entity(
                        0,
                        firstName,
                        lastName,
                        After7DaysDate,
                        currentDate,
                        globalEmail,
                        AppCompatResources.getDrawable(this@UserInfo, R.drawable.defaultimage)!!.toBitmap()
                    )
                )
            }


        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.LoadingUserInfo)
                .setCancelable(true).build()
        dialogue?.show()
        userInfoViewModel.insertResultLiveData?.observe(this@UserInfo, Observer {
            dialogue?.dismiss()
            binding.apply {
                userFirstName.setText("")
                userLastName.setText("")
            }
            startActivity(Intent(this@UserInfo, TrustedNumberDetails::class.java))
            finish()
        })

    }

    private suspend fun getBitmap(imageUri: String): Bitmap {
        val loading = ImageLoader(this)
        val request = ImageRequest.Builder(this)
            .data(imageUri)
            .build()

        val result = (loading.execute(request) as SuccessResult).drawable
        return (result as BitmapDrawable).bitmap
    }


}