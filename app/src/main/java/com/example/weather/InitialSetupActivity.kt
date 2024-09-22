package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
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
import com.example.weather.databinding.ActivityInitialSetupBinding
import com.example.weather.databinding.FirstTimeAlertBinding
import com.example.weather.model.Coord
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.Locale

class InitialSetupActivity : AppCompatActivity() {
    lateinit var binding: ActivityInitialSetupBinding
    lateinit var alertBinding: FirstTimeAlertBinding
    private lateinit var fusedClient : FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        //setContentView(R.layout.activity_initial_setup)
        binding = ActivityInitialSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        supportActionBar?.hide()
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        showFirstTimeCustomAlert()
        /*if (isFirstTime(this)) {
            Log.i("TAG", "onCreate: Alert")
            showFirstTimeCustomAlert()
            //setFirstTimeFlag(this, false)
        }*/
    }

    private fun showFirstTimeCustomAlert() {

        val inflater: LayoutInflater = LayoutInflater.from(this)

        alertBinding = FirstTimeAlertBinding.inflate(inflater)

        val alertDialog = AlertDialog.Builder(this)
            .setView(alertBinding.root)
            .create()

        alertBinding.btnOk.setOnClickListener {
            when (alertBinding.radioGroup.checkedRadioButtonId) {
                alertBinding.gpsBtn.id -> {
                    getGpsLocation()
                    /*val intent = Intent(this, MainActivity::class.java)
                    Log.i("TAG", "showFirstTimeCustomAlert: \nlon : ${coord.longitude} \nlat : ${coord.latitude}")
                    intent.putExtra("lat", coord.latitude)
                    intent.putExtra("lon", coord.longitude)
                    startActivity(intent)*/

                }
                alertBinding.mapBtn.id -> {
                    //doMapAction()
                    val mapFragment = MapFragment()
                    val bundle = Bundle()
                    mapFragment.arguments = bundle
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView3, mapFragment)
                        .commit()
                }
            }

            alertDialog.dismiss()
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
                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "showFirstTimeCustomAlert: \nlon : ${0.0} \nlat : ${0.0}")
                intent.putExtra("lat", 0.0)
                intent.putExtra("lon", 0.0)
                startActivity(intent)
            }
        }
    }
    @SuppressLint("MissingPermission")
    private fun getCurrentLocation(){
        fusedClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                val latitude = location.latitude
                val longitude = location.longitude
                Toast.makeText(this, "Lat: $latitude, Long: $longitude", Toast.LENGTH_SHORT).show()
                Log.i("TAG", "getCurrent Location: \nlon : ${longitude} \nlat : ${latitude}")
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("lat", latitude)
                intent.putExtra("lon", longitude)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Location not available", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                Log.i("TAG", "showFirstTimeCustomAlert: \nlon : ${0.0} \nlat : ${0.0}")
                intent.putExtra("lat", 0.0)
                intent.putExtra("lon", 0.0)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("MissingPermission")
    suspend fun getFreshLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    Log.i("TAG", "onLocationResult: ${p0.locations.get(0).toString()}")
//                    coord.latitude = p0.locations.get(0).latitude
//                    coord.longitude = p0.locations.get(0).longitude

//                    latitudeTxt.text=p0.locations.get(0).latitude.toString()
//                    longtudeTxt.text=p0.locations.get(0).longitude.toString()
                    /*val geocoder = Geocoder(baseContext, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        p0.locations.get(0).latitude,
                        p0.locations.get(0).longitude,
                        1
                    )
                    if (addresses != null && !addresses.isEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0) // Full address
                        //addressTxt.text = addressText

                    }*/


                }
            },
            Looper.myLooper()
        )
    }

    private fun checkPermissions(): Boolean {
        return checkSelfPermission(android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
    }
    fun enableLocationService(){
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
}