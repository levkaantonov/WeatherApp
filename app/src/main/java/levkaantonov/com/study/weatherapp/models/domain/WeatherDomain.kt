package levkaantonov.com.study.weatherapp.models.domain

data class WeatherDomain(
    val consolidated_weather: List<ConsolidatedWeatherDomain>,
    val title: String,
    val woeId: Int
)