package com.rex.lifetracker.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.ActivitySignInBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.view.userUi.UserInfo
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.SignInViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.TrustedContactsViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.UserInfoViewModel
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers


class SignIn : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var signInViewModel: SignInViewModel
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var userInfoViewModel: UserInfoViewModel
    private lateinit var trustedContactsViewModel: TrustedContactsViewModel
    private var isInternetConnected = false
    private var internetDisposable: Disposable? = null
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (intent.getStringExtra("Nuke") == "YES") {
            localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)
            localDataBaseViewModel.nukeTable()
        }

        intiSignInViewModel()
        signInMethod()


        binding.apply {
            GsignIn.setOnClickListener {
                if (isInternetConnected) {
                    getFirebaseDataToLocalDataBase()
                    signIn()
                    return@setOnClickListener
                } else {
                    Toast.makeText(
                        this@SignIn,
                        "Check Your Internet Connection",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@setOnClickListener
                }

            }
        }


    }

    private fun getFirebaseDataToLocalDataBase() {

    }

    private fun signInMethod() {
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun intiSignInViewModel() {

        signInViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(this.application)
        ).get(
            SignInViewModel::class.java
        )
    }


    private var resultLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->


            if (result.resultCode == Activity.RESULT_OK) {
                val intent: Intent? = result.data
                val task = GoogleSignIn.getSignedInAccountFromIntent(intent)
                try {

                    val account = task.getResult(ApiException::class.java)!!
                    Log.d(TAG, "google account: ${account.email}")
                    if (account != null) {

                        //checkEmailExistsOrNot(account.email, account)
                        getGoogleAuthCredential(account)
                    }

                } catch (e: ApiException) {
                    Toast.makeText(this, "Sign In Failed!", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "Error!: $e ")

                }
            }

        }


    private fun getGoogleAuthCredential(account: GoogleSignInAccount) {
        val googleTokenId = account.idToken
        val authCredential = GoogleAuthProvider.getCredential(googleTokenId, null)
        signInWithGoogle(authCredential)
    }

    private fun signInWithGoogle(authCredential: AuthCredential) {
        signInViewModel.signInWithGoogle(authCredential)
        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.SignIn)
                .setCancelable(false)
                .build()
        dialogue?.show()
        signInViewModel.authenticateUserLiveData!!.observe(this,
            { s ->
                dialogue?.dismiss()
                Log.d(TAG, "signInWithGoogle: id $s")
                startActivity(Intent(this, UserInfo::class.java))
                finish()
            })

    }


    private fun signIn() {
        resultLauncher.launch(Intent(googleSignInClient.signInIntent))

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



