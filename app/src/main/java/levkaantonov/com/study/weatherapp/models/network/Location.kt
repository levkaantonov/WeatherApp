package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.models.ui.LocationUI

data class Location(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String,
    val distance: Int = 0
)

fun Location.toUIModel(): LocationUI =
    LocationUI(
        title = this.title,
        location_type = this.location_type,
        woeId = this.woeid,
        latt_long = this.latt_long,
        distance = this.distance
    )