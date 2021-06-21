package levkaantonov.com.study.weatherapp.models.domain

data class LocationDomain (
    val title: String,
    val location_type: String,
    val woeId: Int,
    val latt_long: String,
    val distance: Int = 0
)