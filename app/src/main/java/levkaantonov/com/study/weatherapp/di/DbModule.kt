package levkaantonov.com.study.weatherapp.di

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import levkaantonov.com.study.weatherapp.data.db.AppDatabase
import levkaantonov.com.study.weatherapp.data.db.LocationDao
import levkaantonov.com.study.weatherapp.data.repositories.AppDbRepository
import levkaantonov.com.study.weatherapp.util.DATABASE_NAME
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DbModule {

    @Provides
    @Singleton
    fun provideDataBase(app: Application): AppDatabase {
        return Room.databaseBuilder(app, AppDatabase::class.java, DATABASE_NAME).build()
    }

    @Provides
    fun provideLocationDao(db: AppDatabase): LocationDao {
        return db.locationDao()
    }

    @Provides
    fun provideAppDbRepository(dao: LocationDao): AppDbRepository {
        return AppDbRepository(dao)
    }
}