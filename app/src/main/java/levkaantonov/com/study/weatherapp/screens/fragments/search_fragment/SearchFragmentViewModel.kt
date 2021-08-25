package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import levkaantonov.com.study.weatherapp.data.ApiDataSource
import levkaantonov.com.study.weatherapp.data.DbDataSource
import levkaantonov.com.study.weatherapp.models.common.Resource
import levkaantonov.com.study.weatherapp.models.ui.Location
import levkaantonov.com.study.weatherapp.util.*
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

    private val _favoritesBottomSheetState =
        state.getLiveData(FAVORITES_BOTTOM_SHEET_STATE, FAVORITES_BOTTOM_SHEET_DEFAULT_STATE)
    val favoritesBottomSheetState: LiveData<Int> = _favoritesBottomSheetState

    private val _locations = MutableResourceListLiveData<Location>()
    val locations: ResourceListLiveData<Location> = _searchQuery.switchMap(::onSearchQueryChanged)

    private val _favoritesLocations = MutableLiveData<List<Location>>()
    val favoritesLocation: LiveData<List<Location>> = _favoritesLocations

    fun searchLocations(query: String) {
        _searchQuery.value = query
    }

    fun setSavedQuery(query: String) {
        _savedQuery.value = query
    }

    fun setFavoritesBottomSheetState(state: Int) {
        _favoritesBottomSheetState.value = state
    }

    fun onClickByFavIconInSearchResults(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
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
                _locations.updatePostItemInList(location, newLocation)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun onClickByFavIconInFavoritesResults(location: Location) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                dbDatasource.delete(location.woeId)
                _favoritesLocations.removePostValue(location)
                _locations.updatePostItemInList(
                    location,
                    location.copy(is_favorite = !location.is_favorite)
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun onSearchQueryChanged(query: String): ResourceListLiveData<Location> {
        _locations.value = Resource.Loading()
        if (searchJob.isActive) {
            searchJob.cancel()
        }
        searchJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                val searchingResults = apiDatasource.searchLocations(query)
                val data = markLocationsAccordingToFavorites(searchingResults.data)
                val result = Resource.Success(data)
                _locations.postValue(result)
            } catch (e: Exception) {
                e.printStackTrace()
                _locations.postValue(Resource.Error(e.localizedMessage))
            }
        }
        return _locations
    }

    private fun markLocationsAccordingToFavorites(
        locations: List<Location>?
    ): List<Location> {
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
        private const val FAVORITES_BOTTOM_SHEET_STATE = "favorite_bottom_sheet_state"
        private const val FAVORITES_BOTTOM_SHEET_DEFAULT_STATE = 4
    }
}