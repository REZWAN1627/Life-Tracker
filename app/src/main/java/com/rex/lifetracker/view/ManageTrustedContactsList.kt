package com.rex.lifetracker.view

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import com.rex.lifetracker.R
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SIM_Entity
import com.rex.lifetracker.RoomDataBase.LocalDataBase_Entity.SOSContacts_Entity
import com.rex.lifetracker.adapter.TrustedContactsManageAdapter
import com.rex.lifetracker.databinding.ActivityManageTrustedContactsListBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.viewModel.LocalDataBaseVM.LocalDataBaseViewModel
import com.rex.lifetracker.viewModel.firebaseViewModel.TrustedContactsViewModel
import dmax.dialog.SpotsDialog
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

class ManageTrustedContactsList : AppCompatActivity() {
    private lateinit var trustedContactsViewModel: TrustedContactsViewModel
    private lateinit var binding: ActivityManageTrustedContactsListBinding
    private lateinit var mAdapter: TrustedContactsManageAdapter
    private lateinit var localDataBaseViewModel: LocalDataBaseViewModel
    private var isInternetConnected = false
    private var internetDisposable: Disposable? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityManageTrustedContactsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViewModel()

        setValue()


        //recycler initialize
        binding.apply {
            manageTrustedPeople.apply {
                layoutManager = LinearLayoutManager(this@ManageTrustedContactsList)
                setHasFixedSize(true)
                adapter = mAdapter

            }
        }

        //saving button
        binding.SaveManageBtn.setOnClickListener {
            //not to restart setValue

            Log.d(TAG, "onCreate: button pressed is called")
            val dialogue =
                SpotsDialog.Builder().setContext(this).setTheme(R.style.Saving)
                    .setCancelable(false).build()
            dialogue?.show()
            val value = mAdapter.setSelectedValue(localDataBaseViewModel)
            Log.d(TAG, "onCreate: return value $value")
            if (value) {
                if (isInternetConnected) {
                    localDataBaseViewModel.readAllContacts?.observe(
                        this,
                        Observer { NumberList ->

                            uploadDataToFireBase(NumberList)

                        })

                    dialogue.dismiss()
                } else {
                    localDataBaseViewModel.addSIMSlot(
                        SIM_Entity(
                            0, "0"
                        )
                    )
                    dialogue.dismiss()
                    startActivity(
                        Intent(this, MainActivity::class.java).putExtra(
                            "Service",
                            "NO"
                        )
                    )
                    finish()
                }
            } else {
                startActivity(
                    Intent(this, MainActivity::class.java).putExtra(
                        "Service",
                        "NO"
                    )
                )
                finish()
                dialogue.dismiss()
                return@setOnClickListener
            }
        }
    }


    private fun setValue() {
        val dialogue =
            SpotsDialog.Builder().setContext(this).setTheme(R.style.Custom)
                .setCancelable(true).build()
        dialogue?.show()
        localDataBaseViewModel.readAllContacts?.observe(this, Observer {
            dialogue.dismiss()
            mAdapter.setValue(it)
            Log.e(TAG, "setValue: is called")

        })


    }

    private fun initViewModel() {
        trustedContactsViewModel = ViewModelProvider(this).get(TrustedContactsViewModel::class.java)
        mAdapter = TrustedContactsManageAdapter(this)

        localDataBaseViewModel = ViewModelProvider(this).get(LocalDataBaseViewModel::class.java)


    }


    //--------------------upload firebase-------------------//

    private fun uploadDataToFireBase(NumberList: List<SOSContacts_Entity>?) {

        val mainJob = CoroutineScope(Dispatchers.IO).launch {
            val job2 = launch {
                if (NumberList != null) {
                    if (NumberList.isNotEmpty()) {
                        for (number in NumberList) {
                            if (number.Image != null) {
                                trustedContactsViewModel.updateDataTOFireBase(
                                    number, getByte(number.Image!!)
                                )
                            } else {
                                trustedContactsViewModel.updateDataTOFireBase(
                                    number, null
                                )
                            }
                        }

                    }
                }

            }

        }
        mainJob.invokeOnCompletion {
            Log.d(TAG, "uploadDataToFireBase: done")
            startActivity(
                Intent(this, MainActivity::class.java).putExtra(
                    "Service",
                    "NO"
                )
            )
            finish()

        }

    }


    //---------------Network-----------------//
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


    //-----------------image Convert ------------------------//

    private fun getByte(image: Bitmap): ByteArray {
        Log.d(TAG, "getByte: is called")
        val baos = ByteArrayOutputStream()
        val bitmap = image
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        return baos.toByteArray()

    }


}