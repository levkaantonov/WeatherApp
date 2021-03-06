package levkaantonov.com.study.weatherapp.screens.fragments.details_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil.ItemCallback
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import levkaantonov.com.study.weatherapp.databinding.ItemDetailsBinding
import levkaantonov.com.study.weatherapp.models.ui.ConsolidatedWeather
import levkaantonov.com.study.weatherapp.models.enums.WeatherType

internal class DetailsAdapter :
    ListAdapter<ConsolidatedWeather, DetailsAdapter.ConsolidatedWeatherViewHolder>(
        ConsolidatedWeatherDiffUtil()
    ) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ConsolidatedWeatherViewHolder {
        return ConsolidatedWeatherViewHolder(
            ItemDetailsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ConsolidatedWeatherViewHolder, position: Int) {
        val weather = getItem(position)
        holder.bind(weather)
    }

    class ConsolidatedWeatherViewHolder(private val binding: ItemDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ConsolidatedWeather) {
            binding.tvTemperature.text = item.the_temp
            binding.tvDate.text = item.applicable_date
            val key = item.weather_state_abbr
            binding.ivWeather.setImageResource(WeatherType.getIcon(key).resId)
        }
    }
}

private class ConsolidatedWeatherDiffUtil : ItemCallback<ConsolidatedWeather>() {
    override fun areItemsTheSame(
        oldItem: ConsolidatedWeather,
        newItem: ConsolidatedWeather
    ): Boolean =
        newItem.id == oldItem.id

    override fun areContentsTheSame(
        oldItem: ConsolidatedWeather,
        newItem: ConsolidatedWeather
    ): Boolean =
        newItem == oldItem

}