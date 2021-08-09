package levkaantonov.com.study.weatherapp.data

import levkaantonov.com.study.weatherapp.data.repositories.AppDbRepository
import levkaantonov.com.study.weatherapp.models.db.toUIModel
import levkaantonov.com.study.weatherapp.models.ui.LocationUI
import levkaantonov.com.study.weatherapp.models.ui.toDbModel
import javax.inject.Inject

class DbDataSource @Inject constructor(
    private val appDbRepository: AppDbRepository
) {
    suspend fun getAll(): List<LocationUI> {
        return appDbRepository.getAll().map { it.toUIModel() }
    }

    suspend fun insert(location: LocationUI) {
        appDbRepository.insert(location.toDbModel())
    }

    suspend fun delete(woeId: Int) {
        appDbRepository.delete(woeId)
    }
}