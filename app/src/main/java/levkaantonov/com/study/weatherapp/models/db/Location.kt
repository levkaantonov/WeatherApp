package levkaantonov.com.study.weatherapp.models.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import levkaantonov.com.study.weatherapp.models.ui.Location as UiLocation

@Entity(tableName = "locations")
data class Location(
    @PrimaryKey val woeId: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "location_type") val location_type: String,
    @ColumnInfo(name = "latt_long") val latt_long: String,
    @ColumnInfo(name = "distance") val distance: Int = 0,
    @ColumnInfo(name = "is_favorite") val is_favorite: Boolean = true
)

fun Location.toUiModel():UiLocation =
    UiLocation(
        title = this.title,
        location_type = this.location_type,
        woeId = this.woeId,
        latt_long = this.latt_long,
        distance = this.distance,
        is_favorite = this.is_favorite
    )