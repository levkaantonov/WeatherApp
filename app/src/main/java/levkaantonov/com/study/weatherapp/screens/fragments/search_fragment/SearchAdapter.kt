package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import levkaantonov.com.study.weatherapp.databinding.ItemWheatherBinding
import levkaantonov.com.study.weatherapp.models.Location

class SearchAdapter(clickListener: LocationItemClickListener) :
    ListAdapter<Location, ViewHolder>(LocationDiffUtil()) {
    private val listener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LocationViewHolder(
            ItemWheatherBinding.inflate(
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
        private val binding: ItemWheatherBinding
    ) : ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                listener.click(adapterPosition)
            }
        }

        fun bind(item: Location) {
            binding.idTown.text = item.title
            binding.idCountry.text = item.location_type
        }
    }
}

interface LocationItemClickListener {
    fun click(position: Int)
}

private class LocationDiffUtil : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.title == newItem.title && oldItem.location_type == newItem.location_type
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}
