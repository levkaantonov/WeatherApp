package levkaantonov.com.study.weatherapp.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import levkaantonov.com.study.weatherapp.models.Location
import levkaantonov.com.study.weatherapp.util.Resource
import retrofit2.Response
import javax.inject.Inject

class LocationRepository @Inject constructor(
    private val api: MetaWeatherApi
) {
    private val locations = MutableLiveData<Resource<List<Location>>>()

    suspend fun searchLocations(title: String): LiveData<Resource<List<Location>>> {
        locations.value = Resource.Loading()
        try {
            val response = api.searchLocations(title)
            locations.value = getResourceFromResponse(response)
        } catch (e: Exception) {
            val msg = e.localizedMessage ?: "unknown error"
            locations.value = Resource.Error(msg)
        }
        return locations
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

    private fun generateData(): List<Location> {
        return listOf(
            Location(
                "San Francisco",
                "City",
                2487956,
                "37.777119, -122.41964"
            ),
            Location(
                "San Diego",
                "City",
                2487889,
                "32.715691,-117.161720"
            ),
            Location(
                "San Jose",
                "City",
                2488042,
                "37.338581,-121.885567"
            ),
            Location(
                "San Antonio",
                "City",
                2487796,
                "29.424580,-98.494614"
            ),
            Location(
                "Santa Cruz",
                "City",
                2488853,
                "36.974018,-122.030952"
            ),
            Location(
                "Santiago",
                "City",
                349859,
                "-33.463039,-70.647942"
            ),
            Location(
                "Santorini",
                "City",
                56558361,
                "36.406651,25.456530"
            ),
            Location(
                "Santander",
                "City",
                773964,
                "43.461498,-3.810010"
            ),
            Location(
                "Busan",
                "City",
                1132447,
                "35.170429,128.999481"
            ),
            Location(
                "Santa Cruz de Tenerife",
                "City",
                773692,
                "28.46163,-16.267059"
            ),
            Location(
                "Santa Fe",
                "City",
                2488867,
                "35.666431,-105.972572"
            )
        )
    }
}