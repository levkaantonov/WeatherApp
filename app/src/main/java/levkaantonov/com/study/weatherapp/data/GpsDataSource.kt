package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.GpsRepository
import levkaantonov.com.study.weatherapp.models.ui.Address
import javax.inject.Inject

class GpsDataSource @Inject constructor(
    private val gpsRepository: GpsRepository
) {
    internal suspend fun getCurrentLocation(
        listener: (Address?) -> Unit
    ) {
        gpsRepository.getCurrentLocation { address -> listener(Address(address?.locality)) }
    }

    internal suspend fun getStateOfLocation(
        listener: (Boolean) -> Unit
    ) {
        gpsRepository.getStateOfLocation { state -> listener(state) }
    }
}