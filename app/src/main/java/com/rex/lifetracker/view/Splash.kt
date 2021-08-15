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
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class Splash : AppCompatActivity() {
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var binding: ActivitySplashBinding
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

                goToMainActivity()
            } else {
                goToSignInActivity()
            }

        })

    }

    private fun goToMainActivity() {

        localDataBaseViewModel.readAllContacts.observe(this, { list ->
            if (list.isEmpty()) {
                goToSignInActivity()
            } else {
                if (list.size > 1) {
                    mainActivity()
                } else {
                    goToSignInActivity()
                }

            }
        })


    }

    private fun mainActivity() {
        Log.d(TAG, "mainActivity: is called")
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

    private fun goToSignInActivity() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(
                Intent(
                    this,
                    SignIn::class.java
                ).putExtra("Nuke", "NO")
            )
            finish()

        }, 1000)
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
                        checkIfUserIsAuthenticated()
                    }
                    else -> {
                        localDataBaseViewModel.readAllContacts.observe(
                            this,
                            { list ->
                                if (list.isEmpty()) {
                                    goToSignInActivity()

                                } else {
                                    if (list.size > 1) {
                                        //trailCalculation()
                                        mainActivity()

                                    } else {
                                        goToSignInActivity()
                                    }

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