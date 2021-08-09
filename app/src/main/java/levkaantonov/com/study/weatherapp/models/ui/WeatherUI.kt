package levkaantonov.com.study.weatherapp.models.ui

data class WeatherUI(
    val consolidated_weather: List<ConsolidatedWeatherUI>,
    val title: String,
    val woeId: Int
)