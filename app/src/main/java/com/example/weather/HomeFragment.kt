package com.example.weather

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weather.databinding.FragmentHomeBinding
import com.example.weather.model.Coord
import com.example.weather.viewModel.HomeViewModel

class HomeFragment : Fragment() {
    lateinit var binding : FragmentHomeBinding
    lateinit var viewModel: HomeViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var lon = arguments?.getDouble("lon")
        var lat = arguments?.getDouble("lat")
        lateinit var coord : Coord
        if (lon != null && lat!= null) {
            coord = Coord(lon, lat)
            if (lon == 0.0 && lat == 0.0){
                //todo Error getting location
            }else{

            }
        }


    }

}