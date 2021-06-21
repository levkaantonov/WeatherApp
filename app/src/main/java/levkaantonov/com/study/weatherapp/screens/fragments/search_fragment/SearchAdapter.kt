package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import levkaantonov.com.study.weatherapp.databinding.ItemWeatherBinding
import levkaantonov.com.study.weatherapp.models.domain.LocationDomain

class SearchAdapter(clickListener: LocationItemClickListener) :
    ListAdapter<LocationDomain, ViewHolder>(LocationDiffUtil()) {
    private val listener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LocationViewHolder(
            ItemWeatherBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val location = getItem(position)
        (holder as LocationViewHolder).bind(location)
    }

    inner class LocationViewHolder(
        private val binding: ItemWeatherBinding
    ) : ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if (adapterPosition == -1) {
                    return@setOnClickListener
                }
                val item = getItem(adapterPosition)

                listener.click(item.woeId)
            }
        }

        fun bind(item: LocationDomain) {
            binding.idTown.text = item.title
            binding.idLattLong.text = item.latt_long
        }
    }
}

interface LocationItemClickListener {
    fun click(woeId: Int)
}

private class LocationDiffUtil : DiffUtil.ItemCallback<LocationDomain>() {
    override fun areItemsTheSame(oldItem: LocationDomain, newItem: LocationDomain): Boolean {
        return oldItem.title == newItem.title && oldItem.location_type == newItem.location_type
    }

    override fun areContentsTheSame(oldItem: LocationDomain, newItem: LocationDomain): Boolean {
        return oldItem == newItem
    }
}
