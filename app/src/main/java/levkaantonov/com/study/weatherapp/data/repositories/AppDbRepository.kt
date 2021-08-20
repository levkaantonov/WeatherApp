package levkaantonov.com.study.weatherapp.data.repositories

import levkaantonov.com.study.weatherapp.data.db.LocationDao
import levkaantonov.com.study.weatherapp.models.db.Location
import javax.inject.Inject

class AppDbRepository @Inject constructor(
    private val locationDao: LocationDao
) {
    suspend fun getAll(): List<Location> {
        return locationDao.getAll()
    }

    suspend fun insert(location: Location) {
        locationDao.insert(location)
    }

    suspend fun delete(woeId: Int) {
        locationDao.delete(woeId)
    }
}