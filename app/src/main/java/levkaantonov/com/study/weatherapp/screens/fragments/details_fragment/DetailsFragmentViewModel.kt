package levkaantonov.com.study.weatherapp.screens.fragments.details_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import levkaantonov.com.study.weatherapp.data.ApiDataSource
import levkaantonov.com.study.weatherapp.models.common.Resource
import levkaantonov.com.study.weatherapp.models.ui.Weather
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val dataSource: ApiDataSource
) : ViewModel() {
    private val _weather = MutableLiveData<Resource<Weather>>()
    val weather: LiveData<Resource<Weather>> = _weather

    fun getWeather(woeId: Int) {
        _weather.value = Resource.Loading()
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weather.postValue(dataSource.getWeather(woeId))
            } catch (e: Exception) {
                e.printStackTrace()
                _weather.postValue(Resource.Error(e.localizedMessage))
            }
        }
    }
}