package levkaantonov.com.study.weatherapp.models.ui

import levkaantonov.com.study.weatherapp.models.db.Location as DbLocation

data class Location(
    val title: String,
    val location_type: String,
    val woeId: Int,
    val latt_long: String,
    val distance: Int = 0,
    var is_favorite: Boolean = false
) {
    companion object {
        fun Location.toDbModel(): DbLocation =
            DbLocation(
                title = title,
                location_type = location_type,
                woeId = woeId,
                latt_long = latt_long,
                distance = distance,
                is_favorite = is_favorite
            )
    }
}



