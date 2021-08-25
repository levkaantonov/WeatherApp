package levkaantonov.com.study.weatherapp.di

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.SettingsClient
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import levkaantonov.com.study.weatherapp.data.GpsDataSource
import levkaantonov.com.study.weatherapp.data.repositories.GpsRepository
import java.util.*

@Module
@InstallIn(ViewModelComponent::class)
object GpsModule {
    private const val INTERVAL = 10000L
    private const val FAST_INTERVAL = 5000L

    @Provides
    fun provideGpsDataSource(gpsRepository: GpsRepository): GpsDataSource {
        return GpsDataSource(gpsRepository)
    }

    @Provides
    fun provideGpsRepository(
        locationProviderClient: FusedLocationProviderClient,
        settingsClient: SettingsClient,
        geocoder: Geocoder,
        locationRequest: LocationRequest
    ): GpsRepository {
        return GpsRepository(locationProviderClient, settingsClient, geocoder, locationRequest)
    }

    @Provides
    fun provideFusedLocationProviderClient(
        @ApplicationContext context: Context
    ): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideGeoCoder(
        @ApplicationContext context: Context
    ): Geocoder {
        return Geocoder(context, Locale.ENGLISH)
    }

    @Provides
    fun provideLocationRequest(): LocationRequest {
        return LocationRequest.create().apply {
            interval = INTERVAL
            fastestInterval = FAST_INTERVAL
            priority = LocationRequest.PRIORITY_LOW_POWER
        }
    }

    @Provides
    fun provideSettingsClient(
        @ApplicationContext context: Context
    ): SettingsClient {
        return LocationServices.getSettingsClient(context)
    }
}