package com.example.weather.Settings

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.util.Locale
import androidx.navigation.fragment.findNavController
import com.example.weather.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

class SettingsFragment : Fragment() {

    private lateinit var languageSwitch: Switch
    private lateinit var modeRadioGroup: RadioGroup
    private lateinit var unitRadioGroup: RadioGroup
    private lateinit var radioMap: RadioButton
    private lateinit var radioGps: RadioButton
    private lateinit var radioCelsius: RadioButton
    private lateinit var radioKelvin: RadioButton
    private lateinit var radioFahrenheit: RadioButton
    private lateinit var fusedClient : FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        languageSwitch = view.findViewById(R.id.language_switch)
        modeRadioGroup = view.findViewById(R.id.mode_radio_group)
        radioMap = view.findViewById(R.id.radio_map)
        radioGps = view.findViewById(R.id.radio_gps)
        unitRadioGroup = view.findViewById(R.id.unit_radio_group)
        radioCelsius = view.findViewById(R.id.cel)
        radioKelvin = view.findViewById(R.id.kel)
        radioFahrenheit = view.findViewById(R.id.feh)

        // Load the current language setting
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val isArabic = sharedPreferences.getString("lang", "en")
        languageSwitch.isChecked = (isArabic == "ar")

        // Load the selected mode
        checkSelectMode()
        checkSelectedUnit()
        languageSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                changeLanguage("ar")
                languageSwitch.text = "English" // Change switch text to English
            } else {
                changeLanguage("en")
                languageSwitch.text = "العربية" // Change switch text to Arabic
            }
        }
        unitRadioGroup.setOnCheckedChangeListener { group, checkedId ->
            var unit : String
            if (checkedId == R.id.cel) {
                unit = "c"
            }else if (checkedId == R.id.kel) {
                unit = "k"
            } else {
                unit = "f"
            }
            sharedPreferences.edit().putString("unit", unit).apply()
            requireActivity().recreate()
        }
        modeRadioGroup.setOnCheckedChangeListener { group, checkedId ->

            if (isConnected(requireContext())) {
                var mode : String
                if (checkedId == R.id.radio_map) {
                    mode = "Map"
                    val action = SettingsFragmentDirections.actionSettingsFragmentToMapFragment("set")
                    // Navigate to MapFragment with the argument
                    findNavController().navigate(action)
                    requireActivity().recreate()
                } else {
                    mode = "GPS"
                    getGpsLocation()
                }
                sharedPreferences.edit().putString("mode", mode).apply()
            }else{
                checkSelectMode()
                Toast.makeText(requireContext(), "Connect to Network", Toast.LENGTH_SHORT).show()
            }
            // Save the selected mode in SharedPreferences

        }
    }
    fun checkSelectMode(){
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val selectedMode = sharedPreferences.getString("mode", "Map") // Default to Map
        if (selectedMode == "Map") {
            radioMap.isChecked = true
        } else {
            radioGps.isChecked = true
        }
    }
    fun checkSelectedUnit(){
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        val selectedMode = sharedPreferences.getString("unit", "c") // Default to Map
        if (selectedMode == "c") {
            radioCelsius.isChecked = true
        } else if(selectedMode == "k"){
            radioKelvin.isChecked = true
        }else{
            radioFahrenheit.isChecked = true
        }
    }

    private fun changeLanguage(languageCode: String) {
        val config = resources.configuration
        config.setLocale(Locale(languageCode))
        resources.updateConfiguration(config, resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val sharedPreferences = requireActivity().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
        sharedPreferences.edit().putString("lang", languageCode).apply()
        // Restart the activity to apply changes
        requireActivity().recreate()
    }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    private fun getGpsLocation() {
        //var coord:Coord = Coord(0.0,0.0)
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                getCurrentLocation()
            } else {
                enableLocationService()
            }
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                2005
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2005) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted, continue with getting the GPS location
                getGpsLocation()
            } else {
                // Permission was denied
                Toast.makeText(requireContext(), "Location permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude.toFloat()
                val longitude = location.longitude.toFloat()

                val sharedPreferences = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                // Converting double to float
                editor.putFloat("lat", latitude)
                editor.putFloat("lon", longitude)
                editor.apply()
                requireActivity().recreate()
                Toast.makeText(requireContext(), "Lat: $latitude, Long: $longitude", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getCurrent Location: \nlon : ${longitude} \nlat : ${latitude}")

            } else {
                Toast.makeText(requireContext(), "Location not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(requireContext(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableLocationService(){
        Toast.makeText(requireContext() , "Turn On Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager =
            requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }

}
