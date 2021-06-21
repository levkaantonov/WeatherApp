package levkaantonov.com.study.weatherapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import levkaantonov.com.study.weatherapp.data.repositories.MetaWeatherRepository
import levkaantonov.com.study.weatherapp.data.api.MetaWeatherApi
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    @Provides
    @Singleton
    fun provideLocationRepository(api: MetaWeatherApi): MetaWeatherRepository {
        return MetaWeatherRepository(api)
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit
            .Builder()
            .baseUrl(MetaWeatherApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideMetaWeatherApi(retrofit: Retrofit): MetaWeatherApi {
        return retrofit.create(MetaWeatherApi::class.java)
    }

}