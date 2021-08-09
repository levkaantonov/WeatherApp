package levkaantonov.com.study.weatherapp.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import levkaantonov.com.study.weatherapp.models.db.LocationDb

@Dao
interface LocationDao {

    @Query("select * from locations")
    suspend fun getAll(): List<LocationDb>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: LocationDb)

    @Query("delete from locations where woeId = :woeId")
    suspend fun delete(woeId: Int)
}