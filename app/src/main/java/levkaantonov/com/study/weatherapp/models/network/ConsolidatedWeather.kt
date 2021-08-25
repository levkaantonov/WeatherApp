package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.util.DEGREE_SYMBOL
import levkaantonov.com.study.weatherapp.util.toDate
import levkaantonov.com.study.weatherapp.models.ui.ConsolidatedWeather as UiConsolidatedWeather

data class ConsolidatedWeather(
    val applicable_date: String,
    val id: Long,
    val the_temp: Double,
    val weather_state_abbr: String
) {
    companion object {
        fun ConsolidatedWeather.toUiModel(): UiConsolidatedWeather =
            UiConsolidatedWeather(
                applicable_date = applicable_date.toDate(),
                id = id,
                the_temp = String.format("%.1f%s", the_temp, DEGREE_SYMBOL)
                    .replace(',', '.'),
                weather_state_abbr = weather_state_abbr
            )
    }
}

