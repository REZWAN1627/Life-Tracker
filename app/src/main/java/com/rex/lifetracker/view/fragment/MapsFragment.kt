package com.rex.lifetracker.view.fragment

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.rex.lifetracker.R
import com.rex.lifetracker.databinding.FragmentMapsBinding
import com.rex.lifetracker.utils.Constant.ACCESS_LOCATION_REQUEST_CODE
import com.rex.lifetracker.utils.Constant.TAG
import java.io.IOException


class MapsFragment : Fragment(R.layout.fragment_maps), OnMapReadyCallback,
    GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {
    private var map: GoogleMap? = null
    private var pathPoints = mutableListOf<Polyline>()
    private lateinit var binding: FragmentMapsBinding
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    var locationRequest: LocationRequest? = null

    var userLocationMarker: Marker? = null
    var userLocationAccuracyCircle: Circle? = null
    private var geocoder: Geocoder? = null


    @SuppressLint("MissingPermission")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentMapsBinding.bind(view)
        binding.mapView.onCreate(savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        locationRequest = LocationRequest.create().setInterval(500).setFastestInterval(500)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        geocoder = Geocoder(requireContext())

        binding.apply {

            mapView.getMapAsync {
                map = it
//                if (map!=null){
//                    map?.isMyLocationEnabled = true
//                }

            }
        }


    }

    private var locationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            // Log.d(TAG, "onLocationResult: location -> $locationResult")
            // Log.d(TAG, "onLocationResult: " + locationResult.lastLocation)
            if (map != null) {
                setUserLocationMarker(locationResult.lastLocation)
            }
        }
    }


    private fun setUserLocationMarker(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (userLocationMarker == null) {
            //Create a new marker
            val markerOptions = MarkerOptions()
            markerOptions.position(latLng)
            markerOptions.icon(bitmapDescriptorFromVector(requireContext(), R.drawable.bikelogomap))
            markerOptions.rotation(location.bearing)
            markerOptions.anchor(0.5.toFloat(), 0.5.toFloat())
            userLocationMarker = map?.addMarker(markerOptions)
            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        } else {
            //use the previously created marker
            userLocationMarker!!.position = latLng
            userLocationMarker!!.rotation = location.bearing
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
        }
        if (userLocationAccuracyCircle == null) {
            val circleOptions = CircleOptions()
            circleOptions.center(latLng)
            circleOptions.strokeWidth(4f)
            circleOptions.strokeColor(Color.argb(255, 255, 0, 0))
            circleOptions.fillColor(Color.argb(32, 255, 0, 0))
            circleOptions.radius(location.accuracy.toDouble())
            userLocationAccuracyCircle = map?.addCircle(circleOptions)
        } else {
            userLocationAccuracyCircle!!.center = latLng
            userLocationAccuracyCircle!!.radius = location.accuracy.toDouble()
        }
    }

    private fun startLocationUpdates() {
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
        map?.isMyLocationEnabled = true
        fusedLocationProviderClient!!.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)
    }

//    private fun subscribeToObservers() {
//
//
//        MotionDetectService.pathPoints.observe(viewLifecycleOwner, {
//            pathPoints = it
//            addLatestPolyline()
//            moveCameraToUser()
//        })
//    }

//    private fun moveCameraToUser() {
//        if (pathPoints.isNotEmpty() && pathPoints.last().isNotEmpty()) {
//            map?.animateCamera(
//                CameraUpdateFactory.newLatLngZoom(
//                    pathPoints.last().last(),
//                    Constant.MAP_ZOOM
//                )
//            )
//        }
//    }

//    private fun addAllPolylines() {
//        for (polyline in pathPoints) {
//            val polylineOptions = PolylineOptions()
//                .color(Constant.POLYLINE_COLOR)
//                .width(Constant.POLYLINE_WIDTH)
//                .addAll(polyline)
//            map?.addPolyline(polylineOptions)
//
//
//        }
//    }


//    private fun addLatestPolyline() {
//        if (pathPoints.isNotEmpty() && pathPoints.last().size > 1) {
//            val preLastLatLng = pathPoints.last()[pathPoints.last().size - 2]
//            val lastLatLng = pathPoints.last().last()
//            val polylineOptions = PolylineOptions()
//                .color(Constant.POLYLINE_COLOR)
//                .width(Constant.POLYLINE_WIDTH)
//                .add(preLastLatLng)
//                .add(lastLatLng)
//            map?.addPolyline(polylineOptions)
//
//
//        }
//    }


    private fun bitmapDescriptorFromVector(context: Context, vectorResId: Int): BitmapDescriptor? {
        return ContextCompat.getDrawable(context, vectorResId)?.run {
            setBounds(0, 0, intrinsicWidth, intrinsicHeight)
            val bitmap =
                Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
            draw(Canvas(bitmap))
            BitmapDescriptorFactory.fromBitmap(bitmap)
        }
    }


    override fun onResume() {
        super.onResume()
        binding.mapView?.onResume()
    }

    override fun onStart() {
        super.onStart()
        binding.mapView?.onStart()
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startLocationUpdates()
        } else {
            // you need to request permissions...
        }
    }

    override fun onStop() {
        super.onStop()
        binding.mapView?.onStop()
        stopLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        binding.mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.mapView?.onSaveInstanceState(outState)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map!!.mapType = GoogleMap.MAP_TYPE_NORMAL

        map!!.setOnMapLongClickListener(this)
        map!!.setOnMarkerDragListener(this)

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                ACCESS_LOCATION_REQUEST_CODE
            )
        }


        try {
            val addresses = geocoder!!.getFromLocationName("london", 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val london = LatLng(address.latitude, address.longitude)
                val markerOptions = MarkerOptions()
                    .position(london)
                    .title(address.locality)
                map!!.addMarker(markerOptions)
                map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(london, 16f))
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableUserLocation() {
        map!!.isMyLocationEnabled = true
    }

    private fun zoomToUserLocation() {
        @SuppressLint("MissingPermission") val locationTask =
            fusedLocationProviderClient!!.lastLocation
        locationTask.addOnSuccessListener { location ->
            val latLng = LatLng(location.latitude, location.longitude)
            map!!.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20f))
            //                mMap.addMarker(new MarkerOptions().position(latLng));
        }
    }

    override fun onMapLongClick(latLng: LatLng) {
        try {
            val addresses = geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                map!!.addMarker(
                    MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true)
                )
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onMarkerDragStart(marker: Marker) {
        Log.d(TAG, "onMarkerDragStart: ")
    }

    override fun onMarkerDrag(marker: Marker) {
        Log.d(TAG, "onMarkerDrag: ")
    }

    override fun onMarkerDragEnd(marker: Marker) {
        val latLng = marker.position
        try {
            val addresses = geocoder!!.getFromLocation(latLng.latitude, latLng.longitude, 1)
            if (addresses.size > 0) {
                val address = addresses[0]
                val streetAddress = address.getAddressLine(0)
                marker.title = streetAddress
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation()
                zoomToUserLocation()
            } else {

            }
        }
    }

}

