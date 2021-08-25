package levkaantonov.com.study.weatherapp.screens.common

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import levkaantonov.com.study.weatherapp.data.GpsDataSource
import levkaantonov.com.study.weatherapp.models.common.GpsEvent
import javax.inject.Inject

@HiltViewModel
class GpsViewModel @Inject constructor(
    private val gpsDataSource: GpsDataSource
) : ViewModel() {

    private var gpsGetStateJob: Job = Job()
    private var gpsGetLocationJob: Job = Job()

    private val _gpsData = MutableLiveData<GpsEvent>()
    val gpsData: LiveData<GpsEvent> = _gpsData

    fun getCurrentLocation() {
        getStateOfGps { getLocation() }
    }

    private suspend fun getLocation() {
        gpsDataSource.getCurrentLocation { address ->
            _gpsData.postValue(GpsEvent.CurrentLocation(address))
        }
    }

    private fun getStateOfGps(onLocationTurnOn: suspend () -> Unit) {
        if (gpsGetStateJob.isActive) {
            gpsGetStateJob.cancel()
        }
        gpsGetStateJob = viewModelScope.launch {
            try {
                gpsDataSource.getStateOfLocation { state ->
                    if (!state) {
                        _gpsData.postValue(GpsEvent.State(state))
                        return@getStateOfLocation
                    }
                    if (gpsGetLocationJob.isActive) {
                        gpsGetLocationJob.cancel()
                    }
                    gpsGetLocationJob = viewModelScope.launch { onLocationTurnOn() }
                }
            } catch (e: Exception) {
                _gpsData.postValue(GpsEvent.Fault(e.localizedMessage))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        gpsGetLocationJob.cancel()
        gpsGetStateJob.cancel()
    }
}