package levkaantonov.com.study.weatherapp.models.mappers

import levkaantonov.com.study.weatherapp.models.domain.ConsolidatedWeatherDomain
import levkaantonov.com.study.weatherapp.models.network.ConsolidatedWeather
import levkaantonov.com.study.weatherapp.util.DEGREE_SYMBOL
import levkaantonov.com.study.weatherapp.util.toDate

fun ConsolidatedWeather.toDomainModel(): ConsolidatedWeatherDomain =
    ConsolidatedWeatherDomain(
        applicable_date = this.applicable_date.toDate(),
        id = this.id,
        the_temp = String.format("%.1f%s", this.the_temp, DEGREE_SYMBOL).replace(',', '.'),
        weather_state_abbr = this.weather_state_abbr
    )