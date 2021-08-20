package levkaantonov.com.study.weatherapp.data.repositories

import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.SettingsClient
import javax.inject.Inject
import levkaantonov.com.study.weatherapp.models.ui.Address as AddressUi

class GpsRepository @Inject constructor(
    private val locationProviderClient: FusedLocationProviderClient,
    private val settingsClient: SettingsClient,
    private val geocoder: Geocoder
) {
    suspend fun getCurrentLocation(
        listener: (AddressUi) -> Unit
    ) {
        try {
            locationProviderClient.lastLocation
                .addOnSuccessListener { location ->
                    location.let {
                        val addresses = geocoder.getFromLocation(
                            it.latitude,
                            it.longitude,
                            1
                        )
                        val address = addresses.firstOrNull()
                        address ?: return@let
                        listener(AddressUi(address.locality))
                    }
                }
        } catch (e: SecurityException) {
            throw e
        }
    }

    suspend fun getStateOfLocation(
        listener: (Boolean) -> Unit
    ) {
        var gpsIsEnabled = false
        var networkIsEnabled = false
        try {
            settingsClient.checkLocationSettings(LocationSettingsRequest.Builder().build())
                .addOnSuccessListener { response ->
                    response.let {
                        it.locationSettingsStates?.let { state ->
                            gpsIsEnabled = state.isGpsPresent
                            networkIsEnabled = state.isNetworkLocationPresent
                        }
                    }
                    listener(gpsIsEnabled && networkIsEnabled)
                }
        } catch (e: Exception) {
            throw e
        }
    }
//    private fun checkGpsIsEnabled() {
//        var gpsIsEnabled = false
//        var networkIsEnabled = false
//
//        try {
//            val builder = LocationSettingsRequest.Builder()
//                .addLocationRequest(locationRequest)
//
//            val settingsClient = LocationServices.getSettingsClient(requireContext())
//
//            val task = settingsClient.checkLocationSettings(builder.build())
//            task.apply {
//                addOnSuccessListener { response ->
//                    response?.let {
//                        it.locationSettingsStates?.let { state ->
//                            gpsIsEnabled = state.isGpsPresent
//                            networkIsEnabled = state.isNetworkLocationPresent
//                        }
//                    }
//
//                    if (response == null || (!gpsIsEnabled && !networkIsEnabled)) {
//                        AlertDialog
//                            .Builder(requireContext())
//                            .setTitle(getString(R.string.enable_gps_service))
//                            .setCancelable(false)
//                            .setPositiveButton(getString(R.string.Ok)) { _, _ ->
//                                val startSettingActivityIntent =
//                                    Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
//                                startActivity(startSettingActivityIntent)
//                            }.setNegativeButton(getString(R.string.Cancel), null)
//                            .show()
//                    } else {
//                        startLocationUpdates()
//                    }
//                }
//            }
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

}