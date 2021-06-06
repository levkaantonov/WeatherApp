package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.util.Log
import androidx.lifecycle.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import levkaantonov.com.study.weatherapp.data.LocationRepository
import levkaantonov.com.study.weatherapp.models.Location
import levkaantonov.com.study.weatherapp.util.Resource
import javax.inject.Inject

@HiltViewModel
class SearchFragmentViewModel @Inject constructor(
    private val repository: LocationRepository
) : ViewModel() {
    private var searchJob: Job = Job()

    private val searchQuery = MutableLiveData<String>()

    val locations: LiveData<Resource<List<Location>>> = searchQuery.switchMap { query ->
        liveData {
            if (query.isNullOrEmpty()) {
//            liveData { Resource.Error<Resource<List<Location>>>("unknown country") }
                emit(Resource.Error<Resource<List<Location>>>("empty query") as Resource<List<Location>>)
            } else {
                if (searchJob.isActive) {
                    searchJob.cancel()
                }
//            liveData {
//                searchJob = viewModelScope.launch {
//                    val response = repository.searchLocations(query)
//                    Log.d(
//                        "TAG",
//                        "searchFragmentViewModel coroutine response.value?.data: ${response.value?.data}"
//                    )
//                    Log.d(
//                        "TAG",
//                        "searchFragmentViewModel coroutine response.value: ${response.value}"
//                    )
//                    Log.d("TAG", "searchFragmentViewModel coroutine response: ${response}")
//                    emitSource(response)
//                }
//            }

                searchJob = viewModelScope.launch {
                    val response = repository.searchLocations(query)
                    Log.d(
                        "TAG",
                        "searchFragmentViewModel coroutine ${response.value} ${response.value?.data}"
                    )
                    emitSource(response)
                }
            }
        }
    }


    fun searchLocations(query: String) {
        searchQuery.postValue(query)
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
