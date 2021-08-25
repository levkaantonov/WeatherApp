package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.MetaWeatherRepository
import levkaantonov.com.study.weatherapp.models.common.Resource
import levkaantonov.com.study.weatherapp.models.network.Location.Companion.toUiModel
import levkaantonov.com.study.weatherapp.models.network.Weather.Companion.toUiModel
import retrofit2.Response
import javax.inject.Inject
import levkaantonov.com.study.weatherapp.models.ui.Location as LocationUi
import levkaantonov.com.study.weatherapp.models.ui.Weather as WeatherUi

class ApiDataSource @Inject constructor(
    private val repository: MetaWeatherRepository
) {
    suspend fun searchLocations(title: String): Resource<List<LocationUi>> {
        return try {
            val response = repository.searchLocations(title)
            getResourceFromResponse(response) { locations ->
                locations.map { it.toUiModel() }
            }
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "unknown error"
            Resource.Error(msg)
        }
    }

    suspend fun getWeather(woeId: Int): Resource<WeatherUi> {
        return try {
            val response = repository.getWeather(woeId)
            getResourceFromResponse(response) { weather -> weather.toUiModel() }
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "unknown error"
            Resource.Error(msg)
        }
    }

    private fun <T> getResourceFromData(data: T?): Resource<T> {
        return if (data != null) {
            Resource.Success(data)
        } else {
            val error = "Empty data"
            Resource.Error(error)
        }
    }

    private fun <T, R> getResourceFromResponse(
        response: Response<T>,
        transform: (T) -> R
    ): Resource<R> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body == null || response.code() == 204) {
                Resource.Error("unknown error")
            } else {
                Resource.Success(transform(body))
            }
        } else {
            val error = response.errorBody()?.string() ?: response.message()
            Resource.Error(error)
        }
    }
}