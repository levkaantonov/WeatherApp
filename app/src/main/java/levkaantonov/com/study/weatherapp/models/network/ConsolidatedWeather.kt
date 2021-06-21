package levkaantonov.com.study.weatherapp.models.network

data class ConsolidatedWeather(
    val applicable_date: String,
    val id: Long,
    val the_temp: Double,
    val weather_state_abbr: String
)