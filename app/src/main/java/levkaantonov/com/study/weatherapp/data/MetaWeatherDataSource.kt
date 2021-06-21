package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.MetaWeatherRepository
import levkaantonov.com.study.weatherapp.models.domain.LocationDomain
import levkaantonov.com.study.weatherapp.models.domain.WeatherDomain
import levkaantonov.com.study.weatherapp.models.mappers.toDomainModel
import levkaantonov.com.study.weatherapp.models.common.Resource
import retrofit2.Response
import javax.inject.Inject

class MetaWeatherDataSource @Inject constructor(
    private val repository: MetaWeatherRepository
) {
    suspend fun searchLocations(title: String): Resource<List<LocationDomain>> {
        return try {
            val data = repository.searchLocations(title)
            getResourceFromData(data?.map { it.toDomainModel() })
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "unknown error"
            Resource.Error(msg)
        }
    }

    suspend fun getWeather(woeId: Int): Resource<WeatherDomain> {
        return try {
            val data = repository.getWeather(woeId)
            getResourceFromData(data?.toDomainModel())
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

    private fun <T> getResourceFromResponse(response: Response<T>): Resource<T> {
        return if (response.isSuccessful) {
            val body = response.body()
            if (body == null || response.code() == 204) {
                Resource.Error("unknown error")
            } else {
                Resource.Success(body)
            }
        } else {
            val error = response.errorBody()?.string() ?: response.message()
            Resource.Error(error)
        }
    }
}