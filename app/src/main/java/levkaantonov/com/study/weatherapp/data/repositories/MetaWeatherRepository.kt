package levkaantonov.com.study.weatherapp.data.repositories

import levkaantonov.com.study.weatherapp.data.api.MetaWeatherApi
import levkaantonov.com.study.weatherapp.models.network.Location
import levkaantonov.com.study.weatherapp.models.network.Weather
import retrofit2.Response
import javax.inject.Inject

class MetaWeatherRepository @Inject constructor(
    private val api: MetaWeatherApi
) {
    suspend fun searchLocations(title: String): Response<List<Location>> {
        return try {
            api.searchLocations(title)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getWeather(woeId: Int): Response<Weather> {
        return try {
            api.getWeather(woeId)
        } catch (e: Exception) {
            throw e
        }
    }
}