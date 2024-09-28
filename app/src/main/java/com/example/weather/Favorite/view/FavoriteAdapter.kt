package com.example.weather.Favorite.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.Favorite.viewModel.FavoriteViewModel
import com.example.weather.R
import com.example.weather.database.model.Favorite
import com.example.weather.databinding.ItemFavoriteBinding

class FavoriteAdapter(var viewModel: FavoriteViewModel, var fragment: FavoriteFragment) : ListAdapter<Favorite, FavoriteAdapter.FavoriteViewHolder>(
    ProductDiffUtilItem()
) {
        lateinit var context: Context
        lateinit var binding : ItemFavoriteBinding
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
            context = parent.context
            //val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
            val inflater: LayoutInflater =
                parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = ItemFavoriteBinding.inflate(inflater , parent ,false)
            return FavoriteViewHolder(binding)
        }

        override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
            val currentWeather = getItem(position)
            holder.binding.locationName.text = currentWeather.locationName
            holder.binding.card.setOnClickListener {
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToHomeFragment(
                    currentWeather.lon.toFloat(),
                    currentWeather.lat.toFloat()
                )
                Navigation.findNavController(fragment.requireActivity(), R.id.nav_host_fragment)
                    .navigate(action)
            }
            holder.binding.deleteFavLocation.setOnClickListener {
                viewModel.deleteFavWeather(currentWeather)
            }

        }

        class FavoriteViewHolder(var binding: ItemFavoriteBinding) : RecyclerView.ViewHolder(binding.root)


        class ProductDiffUtilItem : DiffUtil.ItemCallback<Favorite>() {
            override fun areItemsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem.locationName == newItem.locationName
            }

            override fun areContentsTheSame(oldItem: Favorite, newItem: Favorite): Boolean {
                return oldItem == newItem
            }

        }
}