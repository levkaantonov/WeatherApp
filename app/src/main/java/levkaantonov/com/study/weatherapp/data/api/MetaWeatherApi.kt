package levkaantonov.com.study.weatherapp.data.api

import levkaantonov.com.study.weatherapp.models.network.Location
import levkaantonov.com.study.weatherapp.models.network.Weather
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MetaWeatherApi {

    @GET("api/location/search/?")
    suspend fun searchLocations(@Query("query") title: String): Response<List<Location>>

    @GET("api/location/{woeid}/")
    suspend fun getWeather(@Path("woeid") woeid: Int): Response<Weather>

    companion object {
        val BASE_URL = "https://www.metaweather.com/"
    }
}