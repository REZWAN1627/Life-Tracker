package com.rex.lifetracker.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.RuntimeExecutionException
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentMapsBinding
import com.rex.lifetracker.utils.Constant.GPS_AUTO_START_REQUEST_CODE
import com.rex.lifetracker.utils.Constant.REQUEST_PERMISSION
import com.rex.lifetracker.utils.Constant.TAG

class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback {
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var mGoogleMap: GoogleMap
    private val malaysiaCoordinate = LatLng(4.2105, 101.9758)
    private lateinit var currentLocation: Location
    // LatLng(4.2105, 101.9758)\
    //LatLng(25.0433371, 88.7622475)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentMapsBinding.bind(view)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())


        Log.d(TAG, "Fragmnet Oncreate: is called from")
        getCurrentLocation()


    }


    fun getCurrentLocation() {

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        } else {

            fusedLocationProviderClient.lastLocation
                .addOnSuccessListener { it ->
                    // Got last known location. In some rare situations this can be null.
                    if (it != null) {
                        Log.d(TAG, "getCurrentLocation: ${it.latitude},${it.longitude}")
                        currentLocation = it
                        animateZoomInCamera(
                            LatLng(
                                currentLocation.latitude,
                                currentLocation.longitude
                            )
                        )

                    } else {

                        val builder = LocationSettingsRequest.Builder()
                            .addLocationRequest(reqSetting)

                        val client = LocationServices.getSettingsClient(requireContext())
                        client.checkLocationSettings(builder.build())
                            .addOnCompleteListener { task ->
                                try {
                                    val state: LocationSettingsStates =
                                        task.result!!.locationSettingsStates
                                    Log.d(TAG, task.result!!.toString())
                                    Log.e(
                                        TAG, "LocationSettings: \n" +
                                                " GPS present: ${state.isGpsPresent} \n" +
                                                " GPS usable: ${state.isGpsUsable} \n" +
                                                " Location present: " +
                                                "${state.isLocationPresent} \n" +
                                                " Location usable: " +
                                                "${state.isLocationUsable} \n" +
                                                " Network Location present: " +
                                                "${state.isNetworkLocationPresent} \n" +
                                                " Network Location usable: " +
                                                "${state.isNetworkLocationUsable} \n"
                                    )
                                } catch (e: RuntimeExecutionException) {
                                    Log.e(TAG, "stat gps")
                                    if (e.cause is ResolvableApiException) {
                                        (e.cause as ResolvableApiException).startResolutionForResult(
                                            requireActivity(),
                                            GPS_AUTO_START_REQUEST_CODE
                                        )
                                    }

                                }
                            }

                        val locationUpdates = object : LocationCallback() {
                            override fun onLocationResult(lr: LocationResult) {
                                Log.e(TAG, lr.toString())
                                Log.e(TAG, "Newest Location: " + lr.locations.last())
                                // do something with the new location...
                                animateZoomInCamera(
                                    LatLng(
                                        lr.locations.last().latitude,
                                        lr.locations.last().longitude
                                    )
                                )
                            }
                        }
                        fusedLocationProviderClient.requestLocationUpdates(
                            reqSetting,
                            locationUpdates,
                            null /* Looper */
                        )

                        fusedLocationProviderClient.removeLocationUpdates(locationUpdates)

                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.d(TAG, "onActivityResult: is called")
        if (resultCode == Activity.RESULT_OK) {
            Log.d(TAG, "onActivityResult: first 1")
            if (resultCode == GPS_AUTO_START_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult: in 2")
                getCurrentLocation()
            } else {
                Log.d(TAG, "onActivityResult: has pressed cancel")
                getCurrentLocation()
            }
        } else {
            Log.d(TAG, "onActivityResult: no code")
        }
    }

    private val reqSetting = LocationRequest.create().apply {
        fastestInterval = 10000
        interval = 10000
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        smallestDisplacement = 1.0f
    }

    fun animateZoomInCamera(latLng: LatLng) {
        mGoogleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "onRequestPermissionsResult: is success")
                getCurrentLocation()
            } else {
                Toast.makeText(requireContext(), "need to access map", Toast.LENGTH_SHORT).show()
                // getCurrentLocation()

            }
        }
    }


    override fun onDetach() {
        Log.d(TAG, "Fragment: Deactach is called")
        super.onDetach()
    }

    override fun onStart() {
        Log.d(TAG, "fragment on start: is called")
        super.onStart()
    }

    override fun onDestroyView() {
        Log.d(TAG, "onDestroyView: is called")
        super.onDestroyView()
    }

    override fun onPause() {
        Log.d(TAG, "Fragment: on pause is called")
        super.onPause()
    }

    override fun onResume() {

        Log.d(TAG, "fragment onResume: is called")
        getCurrentLocation()

        super.onResume()
    }


    override fun onMapReady(googleMap: GoogleMap?) {
        Log.d(TAG, "onMapReady: is called")

        // mGoogleMap.uiSettings.isZoomControlsEnabled = true

        mGoogleMap = googleMap!!
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(malaysiaCoordinate))
        animateZoomInCamera(malaysiaCoordinate)
        // mGoogleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
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
        mGoogleMap.isMyLocationEnabled = true

//        if(mLocationManager?.isProviderEnabled(LocationManager.GPS_PROVIDER) == true){
//
//        }
    }
}