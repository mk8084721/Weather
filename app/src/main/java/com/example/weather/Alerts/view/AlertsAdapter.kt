package com.example.weather.Alerts.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.database.model.Alert
import com.example.weather.databinding.ItemAlertBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertsAdapter (var Iview : ICommunicate) : ListAdapter<Alert, AlertsAdapter.AlertsViewHolder>(ProductDiffUtilItem()) {
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
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Example format: 12:08 PM
        val timeString = sdf.format(Date(currentAlert.timeInMillis))

        // Set the formatted time string to the TextView
        holder.binding.alertTitle.text = timeString
        holder.binding.deleteAlert.setOnClickListener{
            Iview.deleteAlert(currentAlert)
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