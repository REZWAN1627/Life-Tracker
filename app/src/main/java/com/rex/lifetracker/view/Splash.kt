package com.rex.lifetracker.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.ViewModelProvider
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.rex.lifetracker.databinding.ActivitySplashBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.userUi.UserInfo
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.TrustedContactsViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class Splash : AppCompatActivity() {
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var binding: ActivitySplashBinding
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var trustedContactsViewModel: TrustedContactsViewModel
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var internetDisposable: Disposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        supportActionBar?.hide()

        initSplashViewModel()


    }

    private fun initSplashViewModel() {
        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
        signInViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(SignInViewModel::class.java)


    }

    private fun checkIfUserIsAuthenticated() {
        signInViewModel.checkAuth()
        signInViewModel.checkAuthenticateLiveData?.observe(this, {

            if (it.isAuth) {
                Log.d(TAG, "initModel: is called")
                userInfoViewModel = ViewModelProvider(
                    this,
                    ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
                ).get(
                    UserInfoViewModel::class.java
                )
                trustedContactsViewModel =
                    ViewModelProvider(this).get(TrustedContactsViewModel::class.java)
                goToMainActivity()
            } else {
                goToSignInActivity()
            }

        })

    }

    private fun goToMainActivity() {


        userInfoViewModel.getUserInfoLiveData.observe(this, { userInfo ->
            trustedContactsViewModel.getContactsLiveData?.observe(
                this,
                { trustedContacts ->


                    if (userInfo != null) {
                        if (userInfo.last_Name == "null" && userInfo.first_Name == "null") {
                            Log.d(TAG, "goToMainActivity: user info null")
                            startActivity(Intent(this, UserInfo::class.java))
                            finish()

                        } else if (userInfo.first_Name == "null" || userInfo.last_Name == "null") {
                            Log.d(TAG, "goToMainActivity: One of info null")
                            startActivity(Intent(this, UserInfo::class.java))
                            finish()
                        } else if (trustedContacts.isEmpty()) {
                            startActivity(Intent(this, TrustedNumberDetails::class.java))
                            finish()
                        } else {
                            Log.d(TAG, "goToMainActivity: is called")
                            checkNumber()

                        }
                    }


                })
        })


    }

    private fun checkNumber() {

        trustedContactsViewModel.getContactsLiveData?.observe(
            this,
            { trustedContacts ->
                var i = 0
                var flag1 = false
                var flag2 = false
                while (i < trustedContacts.size) {
                    when (trustedContacts[i].Priority) {
                        "First" -> {
                            flag1 = true

                        }
                        "Second" -> {
                            flag2 = true
                        }
                        else -> {
                            //do nothing
                        }
                    }
                    i++
                }
                if (flag1 && flag2) {
                    Log.d(TAG, "checkNumber: is checked")
                    startActivity(Intent(this, MainActivity::class.java).putExtra("Service", "NO"))
                    // startActivity(Intent(this, TrustedNumberDetails::class.java))
                    finish()
                } else {
                    //startActivity(Intent(this, TrustedNumberDetails::class.java))
                    startActivity(Intent(this, ManageTrustedContactsList::class.java))
                    finish()
                }

            })
    }

    private fun goToSignInActivity() {
        Log.d(TAG, "goToSignInActivity: is called")
        startActivity(Intent(this, SignIn::class.java).putExtra("Nuke", "NO"))
        //startActivity(Intent(this, MainActivity::class.java))
        finish()
    }


    //---------------------NetWork-------------------//

    override fun onResume() {
        super.onResume()



        internetDisposable = ReactiveNetwork.observeInternetConnectivity()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { isConnectedToInternet ->
                when (isConnectedToInternet) {
                    true -> {

                        Log.d(TAG, "onCreate: has internet")
                        Handler(Looper.getMainLooper()).postDelayed({
                            checkIfUserIsAuthenticated()

                        }, 1000)

                    }
                    else -> {
                        Log.d(TAG, "onResume: no internet")

                        localDataBaseViewModel.readAllContacts.observe(
                            this,
                            { list ->
                                Log.d(TAG, "localdatabase size -> ${list.size}")
                                if (list.isEmpty()) {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(Intent(this, SignIn::class.java))
                                        finish()

                                    }, 1000)

                                } else {
                                    Handler(Looper.getMainLooper()).postDelayed({
                                        startActivity(
                                            Intent(
                                                this,
                                                MainActivity::class.java
                                            ).putExtra("Service", "NO")
                                        )
                                        finish()

                                    }, 1000)

                                }
                            })

                    }
                }
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