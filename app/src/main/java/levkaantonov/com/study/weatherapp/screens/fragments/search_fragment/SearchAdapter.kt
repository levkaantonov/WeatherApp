package levkaantonov.com.study.weatherapp.screens.fragments.search_fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import levkaantonov.com.study.weatherapp.R
import levkaantonov.com.study.weatherapp.databinding.ItemLocationBinding
import levkaantonov.com.study.weatherapp.models.ui.Location

class SearchAdapter(clickListener: LocationItemClickListener) :
    ListAdapter<Location, ViewHolder>(LocationDiffUtil()) {
    private val listener = clickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return LocationViewHolder(
            ItemLocationBinding.inflate(
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
        private val binding: ItemLocationBinding
    ) : ViewHolder(binding.root) {
        init {
            binding.locationNameGroup.setOnClickListener {
                if (adapterPosition == -1) {
                    return@setOnClickListener
                }
                val item = getItem(adapterPosition)
                listener.onClickItem(item.woeId)
            }

            binding.ivIsFavorite.setOnClickListener {
                if (adapterPosition == -1) {
                    return@setOnClickListener
                }
                listener.onClickFavoritesIcon(adapterPosition)
            }
        }

        fun bind(item: Location) {
            binding.tvTown.text = item.title
            binding.tvLattLong.text = item.latt_long
            val img =
                if (item.is_favorite) R.drawable.ic_favorite_clicked else R.drawable.ic_favorite
            binding.ivIsFavorite.setImageResource(img)
        }
    }
}


interface LocationItemClickListener {
    fun onClickItem(id: Int)
    fun onClickFavoritesIcon(id: Int)
}

private class LocationDiffUtil : DiffUtil.ItemCallback<Location>() {
    override fun areItemsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem.title == newItem.title && oldItem.location_type == newItem.location_type
    }

    override fun areContentsTheSame(oldItem: Location, newItem: Location): Boolean {
        return oldItem == newItem
    }
}
