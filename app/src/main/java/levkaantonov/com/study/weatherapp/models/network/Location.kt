package levkaantonov.com.study.weatherapp.models.network

import levkaantonov.com.study.weatherapp.models.ui.Location as UiLocation

data class Location(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String,
    val distance: Int = 0
)

fun Location.toUiModel(): UiLocation =
    UiLocation(
        title = title,
        location_type = location_type,
        woeId = this.woeid,
        latt_long = latt_long,
        distance = distance
    )