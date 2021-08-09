package levkaantonov.com.study.weatherapp.models.ui

import levkaantonov.com.study.weatherapp.models.db.LocationDb

data class LocationUI (
    val title: String,
    val location_type: String,
    val woeId: Int,
    val latt_long: String,
    val distance: Int = 0,
    var is_favorite: Boolean = false
)

fun LocationUI.toDbModel(): LocationDb =
    LocationDb(
        title = this.title,
        location_type = this.location_type,
        woeId = this.woeId,
        latt_long = this.latt_long,
        distance = this.distance,
        is_favorite = this.is_favorite
    )

