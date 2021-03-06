package levkaantonov.com.study.weatherapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import levkaantonov.com.study.weatherapp.models.db.Location

@Database(entities = [Location::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}