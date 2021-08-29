package levkaantonov.com.study.weatherapp.models.common

import levkaantonov.com.study.weatherapp.models.ui.Address

sealed class GpsEvent<out T>(private val content: T) {

    var isHandled: Boolean = false
        private set

    fun getContentIfHandled(): T? {
        return if (!isHandled) {
            isHandled = true
            content
        } else {
            null
        }
    }

    class State(isEnabled: Boolean) : GpsEvent<Boolean>(isEnabled)
    class CurrentLocation(address: Address?) : GpsEvent<Address?>(address)
    class Fault(msg: String) : GpsEvent<String>(msg)
}
