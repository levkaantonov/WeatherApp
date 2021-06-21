package levkaantonov.com.study.weatherapp.models.network

data class Location(
    val title: String,
    val location_type: String,
    val woeid: Int,
    val latt_long: String,
    val distance: Int = 0
)