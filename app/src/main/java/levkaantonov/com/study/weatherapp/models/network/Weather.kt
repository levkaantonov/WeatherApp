package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.models.ui.WeatherUI

data class Weather(
    val consolidated_weather: List<ConsolidatedWeather>,
    val title: String,
    val woeId: Int
)
fun Weather.toUIModel(): WeatherUI =
    WeatherUI(
        consolidated_weather = this.consolidated_weather.map { it.toUIModel() }.orEmpty(),
        title = this.title,
        woeId = this.woeId
    )