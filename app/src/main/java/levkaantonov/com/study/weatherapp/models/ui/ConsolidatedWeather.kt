package levkaantonov.com.study.weatherapp.models.ui

data class ConsolidatedWeather(
    val applicable_date: String,
    val id: Long,
    val the_temp: String,
    val weather_state_abbr: String
)