package levkaantonov.com.study.weatherapp.models.enums

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import levkaantonov.com.study.weatherapp.R

enum class WeatherType(@DrawableRes val resId: Int, @StringRes val descStrId: Int) {
    //Snow
    SN(R.drawable.ic_snow, R.string.Snow),

    //Sleet
    SL(R.drawable.ic_sleet, R.string.Sleet),

    //Hail
    H(R.drawable.ic_hail, R.string.Hail),

    //Thunderstorm
    T(R.drawable.ic_thunderstorm, R.string.Thunderstorm),

    //HeavyRain
    HR(R.drawable.ic_heavy_rain, R.string.HeavyRain),

    //LightRain
    LR(R.drawable.ic_light_rain, R.string.LightRain),

    //Showers
    S(R.drawable.ic_showers, R.string.Showers),

    //HeavyCloud
    HC(R.drawable.ic_heavy_cloud, R.string.HeavyCloud),

    //LightCloud
    LC(R.drawable.ic_light_cloud, R.string.LightCloud),

    //Clear
    C(R.drawable.ic_clear, R.string.Clear),

    //Unknown
    U(R.drawable.ic_unknown_weather, R.string.Unknown);

    companion object {
        fun getIcon(key: String): WeatherType {
            return valueOf(key.uppercase())
        }
    }
}