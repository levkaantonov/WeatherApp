package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import levkaantonov.com.study.weatherapp.data.repositories.GpsRepository
import levkaantonov.com.study.weatherapp.models.common.GpsEvent
import javax.inject.Inject

@HiltViewModel
class GpsViewModel @Inject constructor(
    private val gpsRepository: GpsRepository
) : ViewModel() {

    private var gpsGetStateJob: Job = Job()
    private var gpsGetLocationJob: Job = Job()

    private val _gpsData = MutableLiveData<GpsEvent>()
    val gpsData: LiveData<GpsEvent> = _gpsData

    fun getCurrentLocation() {
        getStateOfGps { getLocation() }
    }

    private suspend fun getLocation() {
        withContext(Dispatchers.IO) {
            try {
                gpsRepository.getCurrentLocation { address ->
                    _gpsData.postValue(GpsEvent.CurrentLocation(address))
                }
            } catch (e: Exception) {
                _gpsData.postValue(GpsEvent.Fault(e.localizedMessage))
            }
        }
    }

    private fun getStateOfGps(onLocationTurnOn: suspend () -> Unit) {
        if (gpsGetStateJob.isActive) {
            gpsGetStateJob.cancel()
        }
        viewModelScope.launch(Dispatchers.IO) {
            try {
                gpsRepository.getStateOfLocation { state ->
                    if (!state) {
                        _gpsData.postValue(GpsEvent.State(state))
                        return@getStateOfLocation
                    }

                    if (gpsGetLocationJob.isActive) {
                        gpsGetLocationJob.cancel()
                    }
                    gpsGetLocationJob = launch { onLocationTurnOn() }
                }
            } catch (e: Exception) {
                _gpsData.postValue(GpsEvent.Fault(e.localizedMessage))
            }
        }
    }

//
//    private val locationCallback by lazy {
//        object : LocationCallback() {
//            override fun onLocationResult(locationResult: LocationResult) {
//                locationResult.locations.let {
//                    val location = it.firstOrNull()
//                    location ?: return
//                    val addresses =
//                        geocoder.getFromLocation(location.latitude, location.longitude, 1)
//                    val first = addresses.first()
//                    _currentAddress.postValue(Address(first.locality))
//                }
//            }
//        }
//    }
//
//    fun getCurrentLocation() {
//        viewModelScope.launch(Dispatchers.IO) {
//            gpsRepository.getCurrentLocation(locationCallback)
//        }
//    }
//
//    fun getStateOfLocation() {
//        viewModelScope.launch(Dispatchers.IO) {
//
//        }
//    }
//
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
//
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