package levkaantonov.com.study.weatherapp.models.common

import levkaantonov.com.study.weatherapp.models.ui.Address

sealed class GpsEvent {
    object StartGetLocation : GpsEvent()
    class State(val isEnabled: Boolean) : GpsEvent()
    class CurrentLocation(val address: Address) : GpsEvent()
    class Fault(val msg: String) : GpsEvent()
}