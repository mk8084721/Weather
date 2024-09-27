package com.example.weather.Alerts

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.Favorite.FavoriteAdapter
import com.example.weather.Favorite.FavoriteFragment
import com.example.weather.Favorite.FavoriteFragmentDirections
import com.example.weather.Favorite.FavoriteViewModel
import com.example.weather.R
import com.example.weather.database.model.Alert
import com.example.weather.database.model.Favorite
import com.example.weather.databinding.ItemAlertBinding
import com.example.weather.databinding.ItemFavoriteBinding

class AlertsAdapter () : ListAdapter<Alert, AlertsAdapter.AlertsViewHolder>(ProductDiffUtilItem()) {
    lateinit var context: Context
    lateinit var binding : ItemAlertBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertsViewHolder {
        context = parent.context
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemAlertBinding.inflate(inflater , parent ,false)
        return AlertsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlertsViewHolder, position: Int) {
        val currentAlert = getItem(position)
        holder.binding.alertTitle.text = currentAlert.title
        holder.binding.deleteAlert.setOnClickListener{
            //delete item
        }
    }

    class AlertsViewHolder(var binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root)


    class ProductDiffUtilItem : DiffUtil.ItemCallback<Alert>() {
        override fun areItemsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Alert, newItem: Alert): Boolean {
            return oldItem == newItem
        }

    }
}