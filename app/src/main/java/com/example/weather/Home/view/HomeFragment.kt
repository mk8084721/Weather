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
import com.example.weather.MapFragmentArgs
import kotlinx.coroutines.cancel
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
                                        //
                                    }
                                    is ApiState.Success ->{
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
                                        //todo Weather Image
                                        //binding.weatherImage
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
                    Log.i("TAG", "its Offline: \nlon : ${args.lon} \nlat : ${args.lat}")
                    var todayDate = LocalDate.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    lifecycleScope.launch {
                        viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                            viewModel.homeWeatherSF.collect { result ->
                                if (result.size>0) {
                                    val currentWeather = result.get(0)
                                    if (isConnected(requireContext())) {
                                        if (currentWeather.date == todayDate) {
                                            showHomeData(currentWeather)
                                        } else {
                                            viewModel.getCurrentWeather(lon, lat)
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
                                            ""
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
    }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}