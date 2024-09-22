package com.example.weather

import android.annotation.SuppressLint
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.Home.HourlyWeatherAdapter
import com.example.weather.Repo.WeatherRepo
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.Coord
import com.example.weather.model.CurrentWeather
import com.example.weather.network.API
import com.example.weather.network.ApiState
import com.example.weather.viewModel.HomeViewModel
import com.example.weather.viewModel.HomeViewModelFactory
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val homeViewModelFactory = HomeViewModelFactory(WeatherRepo(API.retrofitService))
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
        var lon = arguments?.getDouble("lon")
        var lat = arguments?.getDouble("lat")
        var hourlyAdapter = HourlyWeatherAdapter()
        binding.hourlyRV.apply {
            adapter = hourlyAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.HORIZONTAL , false)
            //productAdapter.submitList(readProductsFromFile(this.context,"products.json"))
        }
        if (lon != null && lat!= null) {
            if (lon == 0.0 && lat == 0.0){
                //todo Error getting location
            }else{
                viewModel.getCurrentWeather(lon , lat)
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
                                    binding.locationName.text = currentWeather.name
                                    //todo get current date
//                                    binding.dateTxt.text = LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
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


        }else{
            Log.i("TAG", "on home fragment the lon is null")
        }


    }

}