package com.rex.lifetracker.view.Purchase

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.PersonalInfo_Entity
import com.rex.lifetracker.databinding.ActivityPurchasePackageBinding
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.One_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Six_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.AppsAdminDataModelPackage.Twelve_Month_Pack_Model
import com.rex.lifetracker.model.FireBaseModel.PurchaseModel.PurchaseModel
import com.rex.lifetracker.model.FireBaseModel.UserInfoModel
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.LoadingDialog
import com.rex.lifetracker.view.MainActivity
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.AppsInformationViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.Purchase.PurchaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import java.text.SimpleDateFormat
import java.util.*

class Purchase_Package : AppCompatActivity() {
    private val firebaseAuth = Firebase.auth.currentUser
    private lateinit var binding: ActivityPurchasePackageBinding
    private lateinit var appsInformationViewModel: AppsInformationViewModel
    private lateinit var purchaseViewModel: PurchaseViewModel
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private lateinit var userInfoViewModel: UserInfoViewModel
    private var plan = ""
    private var cost = ""
    private var namePackage = ""
    private var intentPackageName = ""
    private var afterPackageDays = ""
    private var currentDate = ""
    private var currentDate2 = ""
    private val calendar = Calendar.getInstance()
    private val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy")
    private val simpleDateFormat2 = SimpleDateFormat("EEE, d MMM -HH:mm")
    private var broughtPack: String = "null"
    private var status: String = "On going"
    private var isInternetConnected = false
    private var internetDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchasePackageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViewModel()

        intentPackageName = intent.getStringExtra("Package").toString()
        setDetails(intentPackageName)

        binding.apply {
          appsInformationViewModel.getAppsInformationLiveData.observe(this@Purchase_Package,{app->
              purchaseBtn.setOnClickListener {
                  if (TextUtils.isEmpty(referenceNumber.text.toString())) {
                      referenceNumber.error = "Empty Field"
                      return@setOnClickListener
                  } else {

                      if(isInternetConnected){
                          when (intentPackageName) {
                              "OneMonth" -> {
                                  formattingDate(
                                      app?.oneMonthPackModel?.description.toString(),
                                      referenceNumber.text.toString()
                                  )

                              }
                              "SixMonth" -> {
                                  formattingDate(
                                      app?.sixMonthPackModel?.description.toString(),
                                      referenceNumber.text.toString()
                                  )

                              }
                              "TwelveMonth" -> {
                                  formattingDate(
                                      app?.twelveMonthPackModel?.description.toString(),
                                      referenceNumber.text.toString()
                                  )

                              }
                          }

                      }else{
                          Toast.makeText(
                              this@Purchase_Package,
                              "You Cannot Purchase package Without Internet",
                              Toast.LENGTH_SHORT
                          ).show()

                          return@setOnClickListener
                      }


                  }
              }
          })
        }
    }

    private fun updatePackageToLocalDataBase(description: String) {
        localDataBaseViewModel.realAllUserInfo.observe(this, { info ->

            localDataBaseViewModel.addUserInfo(
                PersonalInfo_Entity(
                    info.id,
                    info.First_Name,
                    info.Last_Name,
                    afterPackageDays,
                    currentDate,
                    "$description",
                    broughtPack,
                    status,
                    info.User_Email,
                    info.Image
                )
            )
        })
        startActivity(Intent(this,MainActivity::class.java))
        finish()

    }

    private fun updatePackageToDataBase(reference: String, description: String) {
        LoadingDialog.loadingDialogStart(this@Purchase_Package,R.style.Purchase)
        val data = PurchaseModel(
            firebaseAuth?.email.toString(),
            firebaseAuth?.uid.toString(),
            reference,
            "$namePackage $plan $cost"
        )
        purchaseViewModel.insertData(data, description)

        userInfoViewModel.getUserInfoLiveData.observe(this, { info ->
            LoadingDialog.loadingDialogStop()
            userInfoViewModel.insert(
                UserInfoModel(currentDate, info?.avatar_image.toString(),
                    broughtPack, afterPackageDays, info?.first_Name.toString(), info?.last_Name.toString(), status, "$description"),
            )
            


        })

    }

    private fun setDetails(packageName: String) {
        LoadingDialog.loadingDialogStart(this, R.style.LoadingList)
        appsInformationViewModel.getAppsInformationLiveData.observe(this, { data ->
            binding.apply {
                when (packageName) {
                    "OneMonth" -> {
                        LoadingDialog.loadingDialogStop()
                        oneMonthPlane(data?.oneMonthPackModel)
                    }
                    "SixMonth" -> {
                        LoadingDialog.loadingDialogStop()
                        sixMonthPlane(data?.sixMonthPackModel)
                    }
                    "TwelveMonth" -> {
                        LoadingDialog.loadingDialogStop()
                        twelveMonthPlan(data?.twelveMonthPackModel)
                    }
                    else -> {
                        LoadingDialog.loadingDialogStop()
                    }
                }
            }
        })
    }

    private fun oneMonthPlane(oneMonthPackModel: One_Month_Pack_Model?) {
        binding.apply {
           // plan ="30 Days Plan"
            cost = "BDT " + oneMonthPackModel?.cost
            namePackage = "1 Month Plan Package"
            PackageDetails.text = "$namePackage\n$cost"

        }
    }

    private fun sixMonthPlane(sixMonthPackModel: Six_Month_Pack_Model?) {
        binding.apply {
           // plan = sixMonthPackModel?.days + " Days Plan"
            cost = "BDT " + sixMonthPackModel?.cost
            namePackage = "6 Month Plan Package"
            PackageDetails.text = "$namePackage\n$cost"
        }
    }

    private fun twelveMonthPlan(twelveMonthPackModel: Twelve_Month_Pack_Model?) {
        binding.apply {
          //  plan = twelveMonthPackModel?.days + " Days Plan"
            cost = "BDT " + twelveMonthPackModel?.cost
            namePackage = "12 Month Plan Package"
            PackageDetails.text = "$namePackage\n$cost"
        }
    }

    private fun initViewModel() {
        userInfoViewModel = ViewModelProvider(
            this
        ).get(
            UserInfoViewModel::class.java
        )
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
        purchaseViewModel = ViewModelProvider(this).get(PurchaseViewModel::class.java)

        appsInformationViewModel = ViewModelProvider(this)
            .get(AppsInformationViewModel::class.java)
    }

    private fun formattingDate(description: String, reference: String) {
        Log.d(TAG, "formattingDate: $description")

        when (description) {
            "One Month Pack" -> {
                currentDate = simpleDateFormat.format(calendar.time)
                currentDate2 = simpleDateFormat2.format(calendar.time)
                broughtPack = currentDate2 as String
                calendar.add(Calendar.DATE, 30) // number of days to add
                afterPackageDays = simpleDateFormat.format(calendar.time)

                updatePackageToDataBase(
                    reference,
                    description
                )
                updatePackageToLocalDataBase(description)
            }
            "Six Month Pack" -> {
                currentDate = simpleDateFormat.format(calendar.time)
                currentDate2 = simpleDateFormat2.format(calendar.time)
                broughtPack = currentDate2 as String
                calendar.add(Calendar.DATE, 180) // number of days to add
                afterPackageDays = simpleDateFormat.format(calendar.time)
                updatePackageToDataBase(
                    reference,
                    description
                )
                updatePackageToLocalDataBase(description)
            }
            "Twelve Month Pack" -> {
                currentDate = simpleDateFormat.format(calendar.time)
                currentDate2 = simpleDateFormat2.format(calendar.time)
                broughtPack = currentDate2 as String
                calendar.add(Calendar.DATE, 365) // number of days to add
                afterPackageDays = simpleDateFormat.format(calendar.time)
                updatePackageToDataBase(
                    reference,
                    description
                )
                updatePackageToLocalDataBase(description)

            }

        }


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
}