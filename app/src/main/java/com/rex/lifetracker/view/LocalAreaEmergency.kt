package com.rex.lifetracker.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.*
import com.rex.lifetracker.R
import com.rex.lifetracker.adapter.LocalEmergency_RecyclerView
import com.rex.lifetracker.databinding.ActivityLocalAreaEmergencyBinding
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.ExcelReader
import com.rex.lifetracker.utils.LoadingDialog
import java.util.*

class LocalAreaEmergency : AppCompatActivity() {
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var binding: ActivityLocalAreaEmergencyBinding
    private lateinit var mAdapter: LocalEmergency_RecyclerView
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var state:String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocalAreaEmergencyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inti()

        binding.apply {
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    mAdapter.clear()
                    setDataInAdapter(state,parent?.getItemAtPosition(position).toString())
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

            }
        }

    }

    private fun inti() {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.create().setInterval(5000L).setFastestInterval(5000L)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        mAdapter = LocalEmergency_RecyclerView(this)

        binding.apply {
            localEmergencyList.apply {
                layoutManager = LinearLayoutManager(this@LocalAreaEmergency)
                setHasFixedSize(true)
                adapter = mAdapter

            }
        }
    }


    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            getAddress(locationResult.lastLocation)
            // Log.d(TAG, "onLocationResult: location -> $locationResult")
          //  Log.d(TAG, "onLocationResult: " + locationResult.lastLocation)

        }
    }

    fun getAddress(lastLocation: Location) {
        Log.d(TAG, "getAddress: is called")
        val addresses: List<Address>
        val geocoder = Geocoder(this, Locale.getDefault())

        addresses = geocoder.getFromLocation(
            lastLocation.latitude,
            lastLocation.longitude,
            1
        ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        try {
            val city = addresses[0].locality
            val Division = addresses[0].adminArea // Only if available else return NULL
            Log.d(
                TAG,
                "getAddress: City: $city,\nstate: $Division"

            )
            fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
            initializeSpinnerAdapter(Division.split(" ".toRegex()).toTypedArray()[0])
           state = Division


        } catch (e: Exception) {
            Log.d(TAG, "getAddress: exception $e")
        }


    }

    private fun setDataInAdapter(state: String, thana: String) {
        Log.d(TAG, "setDataInAdapter: is called")
        mAdapter.setData(ExcelReader.readxl(this, state.split(" ".toRegex()).toTypedArray()[0],
            thana
        ))

    }

    private fun initializeSpinnerAdapter(state: String) {

        when(state){
            "Rajshahi" ->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Rajshahi)

                )
                setSpinnerAdapter(adapter)
            }
            "Sylhet"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Sylhet)

                )
                setSpinnerAdapter(adapter)
            }
            "Rangpur"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Rangpur)

                )
                setSpinnerAdapter(adapter)
            }
            "Mymensingh"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Mymensingh)

                )
                setSpinnerAdapter(adapter)
            }
            "Barisal"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Barisal)

                )
                setSpinnerAdapter(adapter)
            }
            "Chittagong"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Chittagong)

                )
                setSpinnerAdapter(adapter)
            }
            "Dhaka"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Dhaka)

                )
                setSpinnerAdapter(adapter)
            }
            "Khulna"->{
                adapter = ArrayAdapter(
                    this, android.R.layout.simple_spinner_item, resources.getStringArray(R.array.Khulna)

                )
                setSpinnerAdapter(adapter)
            }
        }

    }

    private fun setSpinnerAdapter(adapter: ArrayAdapter<String>) {
        binding.apply {
            spinner.adapter = adapter
        }
    }

    override fun onStart() {
        super.onStart()
        startLocationUpdates()
    }

    override fun onStop() {
        super.onStop()
        stopLocationUpdates()
    }
}