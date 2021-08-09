package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.MetaWeatherRepository
import levkaantonov.com.study.weatherapp.models.network.toUIModel
import levkaantonov.com.study.weatherapp.models.ui.LocationUI
import levkaantonov.com.study.weatherapp.models.ui.WeatherUI
import javax.inject.Inject

class ApiDataSource @Inject constructor(
    private val repository: MetaWeatherRepository
) {
    //    suspend fun searchLocations(title: String): Resource<List<LocationUI>> {
//        return try {
//            val data = repository.searchLocations(title)
//            getResourceFromData(data?.map { it.toUIModel() })
//        } catch (e: Exception) {
//            val msg = e.localizedMessage ?: "unknown error"
//            Resource.Error(msg)
//        }
//    }
    suspend fun searchLocations(title: String): List<LocationUI> {
        return try {
            val data = repository.searchLocations(title)
            data?.map { it.toUIModel() }.orEmpty()
        } catch (e: Exception) {
            throw e
        }
    }

    //    suspend fun getWeather(woeId: Int): Resource<WeatherUI> {
//        return try {
//            val data = repository.getWeather(woeId)
//            getResourceFromData(data?.toUIModel())
//        } catch (e: Exception) {
//            val msg = e.localizedMessage ?: "unknown error"
//            LoadState.Error(msg)
//        }
//    }
    suspend fun getWeather(woeId: Int): WeatherUI? {
        return try {
            val data = repository.getWeather(woeId)
            data?.toUIModel()
        } catch (e: Exception) {
            throw e
        }
    }

//    private fun <T> getResourceFromData(data: T?): Resource<T> {
//        return if (data != null) {
//            Resource.Success(data)
//        } else {
//            val error = "Empty data"
//            Resource.Error(error)
//        }
//    }
//
//    private fun <T> getResourceFromResponse(response: Response<T>): Resource<T> {
//        return if (response.isSuccessful) {
//            val body = response.body()
//            if (body == null || response.code() == 204) {
//                Resource.Error("unknown error")
//            } else {
//                Resource.Success(body)
//            }
//        } else {
//            val error = response.errorBody()?.string() ?: response.message()
//            Resource.Error(error)
//        }
//    }
}