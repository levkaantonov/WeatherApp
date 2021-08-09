package levkaantonov.com.study.weatherapp.models.common

//sealed class Resource<T>(
//    val data: T? = null,
//    val msg: String? = null
//) {
//    class Success<T>(data: T) : Resource<T>(data)
//    class Loading<T>(data: T? = null) : Resource<T>(data)
//    class Error<T>(msg: String, data: T? = null) : Resource<T>(data, msg)
//}

sealed class LoadState {
    object Success : LoadState()
    object Loading : LoadState()
    class Error(val msg: String) : LoadState()
}