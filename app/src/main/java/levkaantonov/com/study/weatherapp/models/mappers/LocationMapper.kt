package levkaantonov.com.study.weatherapp.models.mappers

import levkaantonov.com.study.weatherapp.models.domain.LocationDomain
import levkaantonov.com.study.weatherapp.models.network.Location

fun Location.toDomainModel(): LocationDomain =
    LocationDomain(
        title = this.title,
        location_type = this.location_type,
        woeId = this.woeid,
        latt_long = this.latt_long,
        distance = this.distance
    )