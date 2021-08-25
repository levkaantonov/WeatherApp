package levkaantonov.com.study.weatherapp.data.repositories

import android.annotation.SuppressLint
import android.location.Address
import android.location.Geocoder
import android.os.Looper
import com.google.android.gms.location.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class GpsRepository @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient,
    private val geocoder: Geocoder,
    private val locationRequest: LocationRequest
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(
        listener: (Address?) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val callback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        try {
                            if (locationResult.locations.isEmpty()) {
                                listener(null)
                                return
                            }

                            locationResult.locations.first().apply {
                                val address =
                                    geocoder.getFromLocation(latitude, longitude, 1)
                                        .firstOrNull() ?: return

                                if (address.locality.isNullOrEmpty()) {
                                    listener(null)
                                    return
                                }
                                listener(address)
                            }
                        } finally {
                            locationProviderClient.removeLocationUpdates(this)
                        }
                    }
                }
                locationProviderClient.requestLocationUpdates(
                    locationRequest,
                    callback,
                    Looper.getMainLooper()
                )
            } catch (e: Exception) {
                throw e
            }
        }
    }

    suspend fun getStateOfLocation(
        listener: (Boolean) -> Unit
    ) {
        withContext(Dispatchers.IO) {
            try {
                val builder = LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest)
                settingsClient.checkLocationSettings(builder.build())
                    .addOnSuccessListener { response ->
                        var gpsIsEnabled = false
                        var networkIsEnabled = false
                        response?.let {
                            it.locationSettingsStates?.let { state ->
                                gpsIsEnabled = state.isGpsPresent
                                networkIsEnabled = state.isNetworkLocationPresent
                            }
                        }
                        listener(gpsIsEnabled && networkIsEnabled)
                    }.addOnFailureListener {
                        listener(false)
                    }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}