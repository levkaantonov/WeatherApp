package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.AppDbRepository
import levkaantonov.com.study.weatherapp.models.db.Location.Companion.toUiModel
import levkaantonov.com.study.weatherapp.models.ui.Location
import levkaantonov.com.study.weatherapp.models.ui.Location.Companion.toDbModel
import javax.inject.Inject

class DbDataSource @Inject constructor(
    private val appDbRepository: AppDbRepository
) {
    suspend fun getAll(): List<Location> {
        return appDbRepository.getAll().map { it.toUiModel() }
    }

    suspend fun insert(location: Location) {
        appDbRepository.insert(location.toDbModel())
    }

    suspend fun delete(woeId: Int) {
        appDbRepository.delete(woeId)
    }
}