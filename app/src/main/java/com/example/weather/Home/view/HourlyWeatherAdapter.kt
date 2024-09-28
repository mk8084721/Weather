package com.example.weather.Home.view

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weather.databinding.ItemHourlyWeatherBinding
import com.example.weather.network.model.CurrentWeather
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HourlyWeatherAdapter() :
    ListAdapter<CurrentWeather, HourlyWeatherAdapter.HourlyWeatherHolder>(ProductDiffUtilItem()) {
    lateinit var context: Context
    lateinit var binding : ItemHourlyWeatherBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HourlyWeatherHolder {
        context = parent.context
        //val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        val inflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        binding = ItemHourlyWeatherBinding.inflate(inflater , parent ,false)
        return HourlyWeatherHolder(binding)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: HourlyWeatherHolder, position: Int) {
        val currentHourWeather = getItem(position)
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val localDateTime = LocalDateTime.parse(currentHourWeather.dt_txt, formatter)
        holder.binding.hourTxt.text = localDateTime.toLocalTime().toString()
        holder.binding.hourTemp.text = getTemp(currentHourWeather.main.temp)

    }

    class HourlyWeatherHolder(var binding: ItemHourlyWeatherBinding) : RecyclerView.ViewHolder(binding.root)


    class ProductDiffUtilItem : DiffUtil.ItemCallback<CurrentWeather>() {
        override fun areItemsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CurrentWeather, newItem: CurrentWeather): Boolean {
            return oldItem == newItem
        }

    }
    private fun getTemp(weatherTemp: Float): String {
        val sharedPreferences = context.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val unit = sharedPreferences.getString("unit", "c")
        if (unit=="k"){
            return String.format("%.2f", weatherTemp+273.15)
        }else if (unit=="f"){
            return String.format("%.2f", weatherTemp* 1.8 + 32)
        }else{
            return "$weatherTemp"
        }
    }
}