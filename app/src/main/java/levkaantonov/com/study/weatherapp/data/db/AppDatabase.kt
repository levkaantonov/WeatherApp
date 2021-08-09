package levkaantonov.com.study.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import levkaantonov.com.study.weatherapp.models.db.LocationDb

@Database(entities = [LocationDb::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}