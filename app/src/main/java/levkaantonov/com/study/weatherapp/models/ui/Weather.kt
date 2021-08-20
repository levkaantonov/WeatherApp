package levkaantonov.com.study.weatherapp.models.ui

data class Weather(
    val consolidated_weather: List<ConsolidatedWeather>,
    val title: String,
    val woeId: Int
)