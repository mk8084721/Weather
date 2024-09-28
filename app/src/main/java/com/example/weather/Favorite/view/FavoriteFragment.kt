package com.example.weather.Favorite.view

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.weather.Favorite.viewModel.FavoriteViewModel
import com.example.weather.Favorite.viewModel.FavoriteViewModelFactory
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.LocalDataSource
import com.example.weather.databinding.FragmentFavoriteBinding
import com.example.weather.network.API
import kotlinx.coroutines.launch

class FavoriteFragment : Fragment() {
   lateinit var binding : FragmentFavoriteBinding
   lateinit var viewModel : FavoriteViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val favoriteFactory = FavoriteViewModelFactory(WeatherRepo(LocalDataSource(requireContext()), API.retrofitService))
        viewModel = ViewModelProvider(this , favoriteFactory).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentFavoriteBinding.inflate(inflater , container , false)
        binding.fabNavigateToMap.setOnClickListener{
            if(isConnected(requireContext())) {
                val action = FavoriteFragmentDirections.actionFavoriteFragmentToMapFragment("fav")
                // Navigate to MapFragment with the argument
                findNavController().navigate(action)
            }else{
                Toast.makeText(requireContext(),"Connect Internet",Toast.LENGTH_SHORT).show()
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val favoriteAdapter = FavoriteAdapter(viewModel , this)
        viewModel.getAllFavWeather()
        lifecycleScope.launch {
            viewModel.allFavoriteWeatherSF.collect{
                value -> favoriteAdapter.submitList(value)
            }
        }
        binding.favoriteRV.apply {
            adapter = favoriteAdapter
            layoutManager = LinearLayoutManager(requireContext() , LinearLayoutManager.VERTICAL , false)
            //productAdapter.submitList(readProductsFromFile(this.context,"products.json"))
        }
    }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

}