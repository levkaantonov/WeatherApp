package levkaantonov.com.study.weatherapp.models.mappers

import levkaantonov.com.study.weatherapp.models.domain.WeatherDomain
import levkaantonov.com.study.weatherapp.models.network.Weather

fun Weather.toDomainModel(): WeatherDomain =
    WeatherDomain(
        consolidated_weather = this.consolidated_weather.map { it.toDomainModel() }.orEmpty(),
        title = this.title,
        woeId = this.woeId
    )