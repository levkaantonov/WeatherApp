package levkaantonov.com.study.weatherapp.models.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "locations")
data class LocationDb(
    @PrimaryKey val woeId: Int,
    val title: String,
    val location_type: String,
    val latt_long: String,
    val distance: Int = 0
)