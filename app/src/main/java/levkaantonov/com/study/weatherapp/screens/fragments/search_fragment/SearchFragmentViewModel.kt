package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import levkaantonov.com.study.weatherapp.data.ApiDataSource
import levkaantonov.com.study.weatherapp.data.DbDataSource
import levkaantonov.com.study.weatherapp.models.common.LoadState
import levkaantonov.com.study.weatherapp.models.ui.LocationUI
import levkaantonov.com.study.weatherapp.util.addPostValue
import levkaantonov.com.study.weatherapp.util.removePostValue
import levkaantonov.com.study.weatherapp.util.updatePostValue
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val apiDatasource: ApiDataSource,
    private val dbDatasource: DbDataSource,
    state: SavedStateHandle
) : ViewModel() {

    private var searchJob: Job = Job()
    private val _searchQuery = MutableLiveData<String>()

    private val _savedQuery = state.getLiveData(CURRENT_QUERY, EMPTY_QUERY)
    val savedQuery: LiveData<String> = _savedQuery

    private val _loadState = MutableLiveData<LoadState>()
    val loadState: LiveData<LoadState> = _loadState

    private val _locations = MutableLiveData<List<LocationUI>>()
    val locations: LiveData<List<LocationUI>> = _searchQuery.switchMap { query ->
        _loadState.value = LoadState.Loading
        if (searchJob.isActive) {
            searchJob.cancel()
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val searchingResults = apiDatasource.searchLocations(query)
                val markedLocations = markLocationsAccordingToFavorites(searchingResults)
                _locations.postValue(markedLocations)
                _loadState.postValue(LoadState.Success)
            } catch (e: Exception) {
                e.printStackTrace()
                _loadState.postValue(LoadState.Error(e.localizedMessage))
                _locations.postValue(listOf())
            }
        }
        _locations
    }

    private val _favoritesLocations = MutableLiveData<List<LocationUI>>()
    val favoritesLocation: LiveData<List<LocationUI>> = _favoritesLocations

    private val _locationEventChannel = Channel<LocationEvent>()
    val locationEvent: Flow<LocationEvent> = _locationEventChannel.receiveAsFlow()

    fun searchLocations(query: String) {
        _searchQuery.value = query.toString()
    }

    fun setSavedQuery(query: String) {
        _savedQuery.value = query
    }

    fun clickOnFavIconInSearchResults(location: LocationUI) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val newLocation = location.copy(is_favorite = !location.is_favorite)
                if (!location.is_favorite) {
                    if (_favoritesLocations.value != null &&
                        !_favoritesLocations.value!!.contains(newLocation)
                    ) {
                        dbDatasource.insert(newLocation)
                        _favoritesLocations.addPostValue(newLocation)
                    }
                } else {
                    dbDatasource.delete(location.woeId)
                    _favoritesLocations.removePostValue(location)
                }
                _locations.updatePostValue(location, newLocation)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clickOnFavIconInFavoritesResults(location: LocationUI) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                dbDatasource.delete(location.woeId)
                _favoritesLocations.removePostValue(location)
                _locations.updatePostValue(
                    location,
                    location.copy(is_favorite = !location.is_favorite)
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun markLocationsAccordingToFavorites(locations: List<LocationUI>?): List<LocationUI> {
        val mutableLocations = locations?.toMutableList() ?: return emptyList()
        val favorites = _favoritesLocations.value ?: return emptyList()
        val listOfFavoritesWoeIds: List<Int> = favorites.map { it.woeId }
        mutableLocations.forEach {
            if (!listOfFavoritesWoeIds.contains(it.woeId)) {
                return@forEach
            }
            it.is_favorite = !it.is_favorite
        }
        return mutableLocations
    }

    override fun onCleared() {
        super.onCleared()
        searchJob.cancel()
    }

    init {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                _favoritesLocations.postValue(dbDatasource.getAll())
            }
        }
    }

    companion object {
        private const val EMPTY_QUERY = ""
        private const val CURRENT_QUERY = "current_query"
    }

    sealed class LocationEvent {
        data class AfterDeleteLocation(val location: LocationUI) : LocationEvent()
        data class NotifyItemRemoved(val id: Int) : LocationEvent()
        data class NotifyItemChanged(val id: Int) : LocationEvent()
    }
}


//    private val _locations = MutableLiveData<Resource<List<LocationUI>>>()
//    val locations: LiveData<Resource<List<LocationUI>>> = _searchQuery.switchMap { query ->
//        _locations.apply {
//            value = Resource.Loading()
//            if (query.isBlank()) {
//                value = Resource.Error("empty query")
//            } else {
//                if (searchJob.isActive) {
//                    searchJob.cancel()
//                }
//                searchJob = viewModelScope.launch {
//                    value = apiDatasource.searchLocations(query)
//                }
//            }
//        }
//    }