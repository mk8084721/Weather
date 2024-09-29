package com.example.weather.Home.view

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.navigation.fragment.findNavController
import com.example.weather.InitialSetupActivity
import com.example.weather.MainActivity
import com.example.weather.R

class BlankFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_blank, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (locationValid()) {
            val action = BlankFragmentDirections.actionBlankFragmentToHomeFragment()
            findNavController().navigate(action)
        }
        val retryBtn : Button = view.findViewById(R.id.retryBtn)
        retryBtn.setOnClickListener{
            setFirstTimeFlag(requireContext(),true)
            val intent = Intent(requireContext(), InitialSetupActivity::class.java)
            startActivity(intent)
        }
    }
    fun locationValid():Boolean{
        val sharedPreferences = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        // Retrieve the saved string values and convert them back to double
        val latitude = sharedPreferences.getFloat("lat", 0.0f)
        val longitude = sharedPreferences.getFloat("lon", 0.0f)
        if (longitude !=0.0f && latitude!=0.0f){
            return true
        }else
            return false
    }
    fun setFirstTimeFlag(context: Context, isFirstTime: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTime", isFirstTime)
        editor.apply()
    }


}