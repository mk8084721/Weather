package com.example.weather.Home.view

import android.content.Context
import android.location.Geocoder
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.HomeWeather
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.network.API
import com.example.weather.network.ApiState
import com.example.weather.Home.viewModel.HomeViewModel
import com.example.weather.Home.viewModel.HomeViewModelFactory
import com.example.weather.database.model.Hourly
import com.example.weather.network.model.Clouds
import com.example.weather.network.model.Coord
import com.example.weather.network.model.CurrentWeather
import com.example.weather.network.model.Temp
import com.example.weather.network.model.WeatherStatus
import com.example.weather.network.model.Wind
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeViewModelFactory = HomeViewModelFactory(WeatherRepo(LocalDataSource(requireContext()) , API.retrofitService))
        viewModel = ViewModelProvider(this , homeViewModelFactory).get(HomeViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_home, container, false)
        binding = FragmentHomeBinding.inflate(inflater , container , false)
        return binding.root
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args: HomeFragmentArgs by navArgs()
        var(lon , lat)= viewModel.getLocationSHP(requireContext())
        var hourlyAdapter = HourlyWeatherAdapter()
        binding.hourlyRV.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
            //productAdapter.submitList(readProductsFromFile(this.context,"products.json"))
        }
        if (lon != null && lat!= null) {
            if (lon == 0.0f && lat == 0.0f){
                //todo Error getting location
            }else{
                if(args.lat != -1.0f && args.lon != -1.0f){
                    viewModel.getCurrentWeather(args.lon,args.lat)
                    Log.i("TAG", "its Online: \nlon : ${args.lon} \nlat : ${args.lat}")
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.currentWeatherSF.collectLatest{
                                    result ->
                                when(result){
                                    is ApiState.Loading ->{
                                        binding.progressBar.visibility = View.VISIBLE
                                        binding.homePage.visibility = View.GONE
                                    }
                                    is ApiState.Success ->{
                                        binding.progressBar.visibility = View.GONE
                                        binding.homePage.visibility = View.VISIBLE
                                        var currentWeather = result.data
                                        //binding.locationName.text = currentWeather.name
                                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                                        val addresses = geocoder.getFromLocation(args.lat.toDouble(), args.lon.toDouble(), 1)
                                        if (addresses != null && !addresses.isEmpty()) {
                                            val address = addresses[0]
                                            val city = address.locality ?: "Unknown City"
                                            binding.locationName.text = city
                                        }
                                        //todo get current date
//                                    binding.dateTxt.text = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        binding.weatherCondition.text = currentWeather.weather.get(0).description
                                        binding.tempValue.text = currentWeather.main.temp.toString()
                                        binding.weatherImage.setImageResource(WeatherType.fromWeatherID(currentWeather.weather.get(0).icon , requireContext()).iconRes)
                                        binding.weatherCondition.text = WeatherType.fromWeatherID(currentWeather.weather.get(0).icon , requireContext()).weatherDesc
                                        binding.humidity.text = currentWeather.main.humidity.toString()
                                        binding.pressure.text = currentWeather.main.pressure.toString()
                                        binding.wind.text = "${currentWeather.wind.speed} m / s"
                                        binding.clouds.text = currentWeather.clouds.all.toString()
                                    }
                                    is ApiState.Failure->{
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.weatherForecastSF.collectLatest{
                                    result ->
                                when(result){
                                    is ApiState.Loading ->{
                                        //
                                    }
                                    is ApiState.ForecastSuccess ->{
                                        var forecastWeather = result.data

                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        val localDateTime = LocalDateTime.parse(forecastWeather.list[0].dt_txt, formatter)
                                        binding.dateTxt.text = localDateTime.toLocalDate().toString()
                                        //send data to the adabter
                                        hourlyAdapter.submitList(forecastWeather.list.toMutableList())

                                    }
                                    is ApiState.Failure ->{
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
                else {
                    viewModel.getHomeWeather()
                    viewModel.getHourlyWeather()
                    Log.i("TAG", "its Offline: \nlon args : ${args.lon} \nlat args : ${args.lat}")
                    Log.i("TAG", "its Offline: \nlon : ${lon} \nlat : ${lat}")
                    var todayDate = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.homeWeatherSF.collect { result ->
                                if (result.size>0) {
                                    val currentWeather = result.get(0)
                                    if (isConnected(requireContext())) {
                                        if (currentWeather.date == todayDate && currentWeather.lon==lon &&currentWeather.lat == lat) {
                                            showHomeData(currentWeather)
                                        } else {
                                            viewModel.getCurrentWeather(lon, lat)
                                            viewModel.clearHourlyTable()
                                        }
                                    } else {
                                        if (currentWeather.lat == 0.0f && currentWeather.lon == 0.0f) {
                                            // todo error connect the internet
                                        } else {
                                            showHomeData(currentWeather)
                                        }
                                    }
                                }
                            }
                        }
                    }
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.hourlyWeatherSF.collectLatest{
                                    result ->
                                when(result){
                                    is ApiState.Loading ->{
                                        binding.progressBar.visibility = View.VISIBLE
                                        binding.homePage.visibility = View.GONE
                                    }
                                    is ApiState.LocalHourlySuccess ->{
                                        binding.progressBar.visibility = View.GONE
                                        binding.homePage.visibility = View.VISIBLE
                                        val hourlyWeather = result.data
                                        val currentWeatherList = mutableListOf<CurrentWeather>()
                                        if(hourlyWeather.size>0) {
                                            binding.dateTxt.text = hourlyWeather.get(0).date
                                            for (item in hourlyWeather) {
                                                currentWeatherList.add(
                                                    CurrentWeather(
                                                        Coord(0.0f, 0.0f),
                                                        arrayOf<WeatherStatus>(),
                                                        "",
                                                        Temp(
                                                            item.temp,
                                                            0.0f,
                                                            0.0f,
                                                            0.0f,
                                                            0,
                                                            0,
                                                            0,
                                                            0
                                                        ),
                                                        0,
                                                        Wind(0.0f, 0, 0.0f),
                                                        0,
                                                        0,
                                                        "",
                                                        0,
                                                        "${item.date} ${item.hour}:00",
                                                        Clouds(0)
                                                    )
                                                )
                                            }
                                            hourlyAdapter.submitList(currentWeatherList)
                                        }

                                    }
                                    is ApiState.Failure ->{
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.currentWeatherSF.collectLatest{
                                    result ->
                                when(result){
                                    is ApiState.Loading ->{
                                        //
                                    }
                                    is ApiState.Success ->{
                                        var currentWeather = result.data
                                        val geocoder = Geocoder(requireContext(), Locale.getDefault())
                                        var city = "Unknown City"
                                        val addresses = geocoder.getFromLocation(lat.toDouble(), lon.toDouble(), 1)
                                        if (!addresses.isNullOrEmpty()) {
                                            val address = addresses[0]
                                            city = address.locality ?: "Unknown City"
                                        }
                                        viewModel.updateHomeWeather(HomeWeather(
                                            1,
                                            lon,
                                            lat,
                                            city,
                                            todayDate,
                                            currentWeather.weather.get(0).description,
                                            "",
                                            currentWeather.main.temp,
                                            0.0f,
                                            "",
                                            currentWeather.main.pressure,
                                            currentWeather.main.humidity,
                                            currentWeather.wind.speed,
                                            currentWeather.clouds.all
                                        ))
                                    }
                                    is ApiState.Failure->{
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                            viewModel.weatherForecastSF.collectLatest{
                                    result ->
                                when(result){
                                    is ApiState.Loading ->{
                                        //
                                    }
                                    is ApiState.ForecastSuccess ->{
                                        var forecastWeather = result.data.list
                                        var hourlyWeather = mutableListOf<Hourly>()
                                        for (item in forecastWeather){
                                            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                            val localDateTime = LocalDateTime.parse(item.dt_txt, formatter)
                                            hourlyWeather.add(
                                                Hourly(
                                                    localDateTime.toLocalTime().toString(),
                                                    localDateTime.toLocalDate().toString(),
                                                    item.main.temp
                                                )
                                            )
                                        }
                                        viewModel.insertHourlyWeather(hourlyWeather)
                                        /*///////////////////////////////////////////////////

                                        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                                        val localDateTime = LocalDateTime.parse(forecastWeather[0].dt_txt, formatter)
                                        binding.dateTxt.text = localDateTime.toLocalDate().toString()
                                        //send data to the adabter
                                        hourlyAdapter.submitList(forecastWeather.toMutableList())*/

                                    }
                                    is ApiState.Failure ->{
                                        Toast.makeText(
                                            requireContext(),
                                            "Check Internet",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }else{
            Log.i("TAG", "on home fragment the lon is null")
        }

    }


    fun showHomeData(currentWeather: HomeWeather){
        binding.dateTxt.text = currentWeather.date
        binding.locationName.text = currentWeather.locationName
        binding.tempValue.text = currentWeather.weatherTemp.toString()
        binding.weatherCondition.text = currentWeather.weatherConditionEn
        binding.humidity.text = currentWeather.humidity.toString()
        binding.pressure.text = currentWeather.pressure.toString()
        binding.wind.text = currentWeather.windSpeed.toString()
        binding.clouds.text = currentWeather.clouds.toString()
    }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}