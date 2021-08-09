package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.models.ui.ConsolidatedWeatherUI
import levkaantonov.com.study.weatherapp.util.DEGREE_SYMBOL
import levkaantonov.com.study.weatherapp.util.toDate

data class ConsolidatedWeather(
    val applicable_date: String,
    val id: Long,
    val the_temp: Double,
    val weather_state_abbr: String
)

fun ConsolidatedWeather.toUIModel(): ConsolidatedWeatherUI =
    ConsolidatedWeatherUI(
        applicable_date = this.applicable_date.toDate(),
        id = this.id,
        the_temp = String.format("%.1f%s", this.the_temp, DEGREE_SYMBOL)
            .replace(',', '.'),
        weather_state_abbr = this.weather_state_abbr
    )