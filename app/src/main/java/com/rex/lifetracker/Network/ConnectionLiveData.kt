package com.rex.lifetracker.Network

import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkRequest
import android.os.Build
import android.util.Log
import androidx.lifecycle.LiveData
import com.rex.lifetracker.utils.Constant.TAG
import com.rex.lifetracker.utils.DoesNetworkHaveInternet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.net.NetworkInfo

import android.R.attr.name




class ConnectionLiveData(context: Context) : LiveData<Boolean>() {
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback
    private val cm = context.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
    private val validNetworks: MutableSet<Network> = HashSet()

    private fun checkValidNetworks() {
        Log.d(TAG, "checkValidNetworks: is called")
        postValue(validNetworks.size > 0)
    }

    override fun onActive() {

        Log.d(TAG, "onActive: is called")
        networkCallback = createNetworkCallback()
        Log.d(TAG, "onActive: network ")
        val networkRequest = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "onActive: network request")
            NetworkRequest.Builder()
                .addCapability(NET_CAPABILITY_INTERNET)
                .build()
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "onActive: is called if network call back")
            cm.registerNetworkCallback(networkRequest, networkCallback)
        }
    }

    override fun onInactive() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Log.d(TAG, "onInactive: is called")
            postValue(false)
            cm.unregisterNetworkCallback(networkCallback)

        }
    }

    private fun createNetworkCallback() =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            object : ConnectivityManager.NetworkCallback() {

                override fun onUnavailable() {
                    Log.d(TAG, "onUnavailable: is called")
                    super.onUnavailable()
                }

                /*
                                  Called when a network is detected. If that network has internet, save it in the Set.
                                  Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onAvailable(android.net.Network)
                                 */
                override fun onAvailable(network: Network) {
                    Log.d(TAG, "onAvailable: $network")
                    val networkCapabilities = cm.getNetworkCapabilities(network)
                    val hasInternetCapability =
                        networkCapabilities?.hasCapability(NET_CAPABILITY_INTERNET)
                    Log.d(TAG, "onAvailable: ${network}, $hasInternetCapability")
                    if (hasInternetCapability == true) {
                        // check if this network actually has internet
                        CoroutineScope(Dispatchers.IO).launch {
                            val hasInternet = DoesNetworkHaveInternet.execute(network.socketFactory)
                            if (hasInternet) {
                                withContext(Dispatchers.Main) {
                                    Log.d(TAG, "onAvailable: adding network. $network")
                                    validNetworks.add(network)
                                    checkValidNetworks()
                                }
                            }else{
                                withContext(Dispatchers.Main) {
                                    Log.d(TAG, "onAvailable: removing network. $network")
                                    validNetworks.remove(network)
                                    checkValidNetworks()
                                }
                            }
                        }
                    }

                }

                /*
                  If the callback was registered with registerNetworkCallback() it will be called for each network which no longer satisfies the criteria of the callback.
                  Source: https://developer.android.com/reference/android/net/ConnectivityManager.NetworkCallback#onLost(android.net.Network)
                 */
                override fun onLost(network: Network) {
                    Log.d(TAG, "onLost: $network")
                    validNetworks.remove(network)
                    checkValidNetworks()
                }

            }
        } else {
            TODO("VERSION.SDK_INT < LOLLIPOP")
        }


}