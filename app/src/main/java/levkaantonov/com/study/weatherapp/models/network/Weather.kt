package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.models.ui.Weather as UiWeather

data class Weather(
    val consolidated_weather: List<ConsolidatedWeather>,
    val title: String,
    val woeId: Int
)

fun Weather.toUiModel(): UiWeather =
    UiWeather(
        consolidated_weather = consolidated_weather.map { it.toUiModel() }.orEmpty(),
        title = title,
        woeId = woeId
    )