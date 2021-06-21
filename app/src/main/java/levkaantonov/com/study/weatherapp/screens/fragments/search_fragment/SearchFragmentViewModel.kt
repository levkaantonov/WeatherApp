package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import levkaantonov.com.study.weatherapp.data.MetaWeatherDataSource
import levkaantonov.com.study.weatherapp.models.domain.LocationDomain
import levkaantonov.com.study.weatherapp.models.common.Resource
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val dataSource: MetaWeatherDataSource
) : ViewModel() {
    private var searchJob: Job = Job()

    private val searchQuery = MutableLiveData<String>()
    private val _locations = MutableLiveData<Resource<List<LocationDomain>>>()
    val locations: LiveData<Resource<List<LocationDomain>>> = searchQuery.switchMap { query ->
        _locations.apply {
            value = Resource.Loading()
            if (query.isBlank()) {
                value = Resource.Error("empty query")
            } else {
                if (searchJob.isActive) {
                    searchJob.cancel()
                }
                searchJob = viewModelScope.launch {
                    value = dataSource.searchLocations(query)
                }
            }
        }
    }

    fun searchLocations(query: String) {
        searchQuery.value = query
    }

    override fun onCleared() {
        super.onCleared()
        searchJob.cancel()
    }

    companion object {
        private const val EMPTY_QUERY = ""
        private const val CURRENT_QUERY = "current_query"
    }
}
