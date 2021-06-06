package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.models.Location
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface MetaWeatherApi {

    @GET("api/location/search/?")
    suspend fun searchLocations(@Query("query") title: String): Response<List<Location>>

    companion object {
        val BASE_URL = "https://www.metaweather.com/"
    }
}