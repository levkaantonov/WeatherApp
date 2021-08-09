package levkaantonov.com.study.weatherapp.data.repositories

import levkaantonov.com.study.weatherapp.data.api.MetaWeatherApi
import levkaantonov.com.study.weatherapp.models.network.Location
import levkaantonov.com.study.weatherapp.models.network.Weather
import retrofit2.Response
import javax.inject.Inject

class MetaWeatherRepository @Inject constructor(
    private val api: MetaWeatherApi
) {
    suspend fun searchLocations(title: String): List<Location>? {
        return try {
            val response = api.searchLocations(title)
            getDataFromResponse(response)
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun getWeather(woeId: Int): Weather? {
        return try {
            val response = api.getWeather(woeId)
            getDataFromResponse(response)
        } catch (e: Exception) {
            throw e
        }
    }

    private fun <T> getDataFromResponse(response: Response<T>): T? {
        return if (response.isSuccessful) {
            response.body()
        } else {
            null
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