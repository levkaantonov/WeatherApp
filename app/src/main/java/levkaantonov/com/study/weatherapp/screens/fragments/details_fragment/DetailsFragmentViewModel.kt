package levkaantonov.com.study.weatherapp.screens.fragments.details_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import levkaantonov.com.study.weatherapp.data.MetaWeatherDataSource
import levkaantonov.com.study.weatherapp.models.domain.WeatherDomain
import levkaantonov.com.study.weatherapp.models.common.Resource
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val dataSource: MetaWeatherDataSource
) : ViewModel() {
    private val _weather = MutableLiveData<Resource<WeatherDomain>>()
    val weatherLiveData: LiveData<Resource<WeatherDomain>> = _weather

    fun getWeather(woeId: Int) =
        viewModelScope.launch {
            _weather.apply {
                value = dataSource.getWeather(woeId)
            }
        }
}