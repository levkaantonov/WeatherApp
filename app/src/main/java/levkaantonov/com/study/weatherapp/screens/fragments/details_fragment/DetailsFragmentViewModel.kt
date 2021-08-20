package levkaantonov.com.study.weatherapp.screens.fragments.details_fragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import levkaantonov.com.study.weatherapp.data.ApiDataSource
import levkaantonov.com.study.weatherapp.models.ui.Weather
import levkaantonov.com.study.weatherapp.models.common.LoadState
import javax.inject.Inject

@HiltViewModel
class DetailsFragmentViewModel @Inject constructor(
    private val dataSource: ApiDataSource
) : ViewModel() {

    private val _loadState = MutableLiveData<LoadState>()
    val loadState: LiveData<LoadState> = _loadState

    private val _weather = MutableLiveData<Weather?>()
    val weather: LiveData<Weather?> = _weather

    fun getWeather(woeId: Int) {
        _loadState.value = LoadState.Loading
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _weather.postValue(dataSource.getWeather(woeId))
                _loadState.postValue(LoadState.Success)
            } catch (e: Exception) {
                e.printStackTrace()
                _loadState.postValue(LoadState.Error(e.localizedMessage))
                _weather.postValue(null)
            }
        }
    }
}