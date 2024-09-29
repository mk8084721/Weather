package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.HomeWeather
import com.example.weather.databinding.ActivityInitialSetupBinding
import com.example.weather.databinding.FirstTimeAlertBinding
import com.example.weather.network.API
import com.example.weather.Home.viewModel.HomeViewModel
import com.example.weather.Home.viewModel.HomeViewModelFactory
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class InitialSetupActivity : AppCompatActivity() {
    lateinit var binding: ActivityInitialSetupBinding
    lateinit var alertBinding: FirstTimeAlertBinding
    private lateinit var fusedClient : FusedLocationProviderClient
    lateinit var viewModel: HomeViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_initial_setup)
        val homeViewModelFactory = HomeViewModelFactory(WeatherRepo(LocalDataSource(this.baseContext) , API.retrofitService))
        viewModel = ViewModelProvider(this , homeViewModelFactory).get(HomeViewModel::class.java)

        binding = ActivityInitialSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)

        if (isFirstTime(this)) {
            Log.i("TAG", "onCreate: Alert")
            viewModel.insertEmptyHomeWeather(HomeWeather(1,0.0f,0.0f,"","","","",0.0f,0.0f,"",0,0,0.0f,0,""))
            viewModel.insertDefaultSettings(this)
            showFirstTimeCustomAlert()
            setFirstTimeFlag(this, false)
        }else{
            val lang =viewModel.getLangSHP(this)
            val config = resources.configuration
            config.setLocale(lang?.let { Locale(it) })
            resources.updateConfiguration(config, resources.displayMetrics)

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showFirstTimeCustomAlert() {

        val inflater: LayoutInflater = LayoutInflater.from(this)

        alertBinding = FirstTimeAlertBinding.inflate(inflater)

        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertBinding.btnOk.setOnClickListener {
            if(isConnected(this)) {
                when (alertBinding.radioGroup.checkedRadioButtonId) {

                    alertBinding.gpsBtn.id -> {
                        val sharedPreferences =
                            this.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("mode", "GPS").apply()
                        getGpsLocation()

                    }

                    alertBinding.mapBtn.id -> {
                        //doMapAction()
                        val sharedPreferences =
                            this.getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                        sharedPreferences.edit().putString("mode", "Map").apply()
                        val mapFragment = MapFragment()
                        val bundle = Bundle()
                        mapFragment.arguments = bundle
                        supportFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView3, mapFragment)
                            .commit()
                    }
                }
                alertDialog.dismiss()
            }else{
                Toast.makeText(this,"Connect To Internet",Toast.LENGTH_SHORT).show()
            }

        }

        alertDialog.show()
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
                this,
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
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show()
                viewModel.saveLocationSHP(this,0.0f,0.0f)

                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "showFirstTimeCustomAlert: \nlon : ${0.0} \nlat : ${0.0}")
                startActivity(intent)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude.toFloat()
                val longitude = location.longitude.toFloat()
                viewModel.saveLocationSHP(this,longitude,latitude)

                Toast.makeText(this, "Lat: $latitude, Long: $longitude", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getCurrent Location: \nlon : ${longitude} \nlat : ${latitude}")
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                viewModel.saveLocationSHP(this,0.0f,0.0f)
                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "showFirstTimeCustomAlert: \nlon : ${0.0} \nlat : ${0.0}")
                startActivity(intent)
            }
        }
    }

    private fun checkPermissions(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    private fun enableLocationService(){
        Toast.makeText(this , "Turn On Location", Toast.LENGTH_LONG).show()
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }
    private fun isLocationEnabled():Boolean{
        val locationManager: LocationManager =
            getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER)
    }


    // Using Shared Preferance to Detect If It's The First Time
    fun isFirstTime(context: Context): Boolean {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isFirstTime", true)
    }

    fun setFirstTimeFlag(context: Context, isFirstTime: Boolean) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("AppPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putBoolean("isFirstTime", isFirstTime)
        editor.apply()
    }
    fun isConnected(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = cm.getNetworkCapabilities(cm.activeNetwork)
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}