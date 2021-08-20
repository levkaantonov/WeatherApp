package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.app.Application
import android.location.Address
import levkaantonov.com.study.weatherapp.models.ui.Address as AddressUi
import android.location.Geocoder
import androidx.lifecycle.LiveData
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import java.util.*

class GpsLiveData(application: Application) : LiveData<AddressUi>() {
    private val fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(application)
    }
    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.locations.let {
                    val location = it.firstOrNull()
                    location ?: return
                    val geocoder = Geocoder(application, Locale.ENGLISH)
                    val addresses =
                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    setGpsData(addresses.first())
                    stopLocationUpdates()
                }
            }
        }
    }

    private fun setGpsData(address: Address) {
        value = levkaantonov.com.study.weatherapp.models.ui.Address(
            address.locality,
            address.postalCode,
            address.countryCode,
            address.countryName
        )
    }

    override fun onActive() {
        super.onActive()
        startLocationUpdates()
    }

    override fun onInactive() {
        super.onInactive()
        stopLocationUpdates()
    }

    private fun startLocationUpdates() {
        try {
            fusedLocationProviderClient
                .requestLocationUpdates(locationRequest, locationCallback, null)
        } catch (e: SecurityException) {
            throw e
        }
    }

    private fun stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback)
    }


    companion object {
        val locationRequest: LocationRequest =
            LocationRequest.create().apply {
                interval = INTERVAL
                fastestInterval = FAST_INTERVAL
                priority = LocationRequest.PRIORITY_LOW_POWER
            }

        private const val INTERVAL = 10000L
        private const val FAST_INTERVAL = 5000L
    }
}