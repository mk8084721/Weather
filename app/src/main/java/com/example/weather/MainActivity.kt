package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.databinding.FirstTimeAlertBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var alertBinding: FirstTimeAlertBinding
    private lateinit var fusedClient : FusedLocationProviderClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        /*if (isFirstTime(this)) {
            showFirstTimeCustomAlert()
            setFirstTimeFlag(this, false)
        }*/
        showFirstTimeCustomAlert()

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
                }
                alertBinding.mapBtn.id -> {
                    //doMapAction()
                }
            }

            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    private fun getGpsLocation() {
        if (checkPermissions()){
            if(isLocationEnabled()){
                getFreshLocation()
            }else{
                enableLocationService()
            }
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION,android.Manifest.permission.ACCESS_COARSE_LOCATION),2005)
        }
    }

    @SuppressLint("MissingPermission")
    fun getFreshLocation() {
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        fusedClient.requestLocationUpdates(
            LocationRequest.Builder(0).apply {
                setPriority(Priority.PRIORITY_HIGH_ACCURACY)
            }.build(),
            object : LocationCallback() {
                override fun onLocationResult(p0: LocationResult) {
                    super.onLocationResult(p0)
                    Log.i("TAG", "onLocationResult: ${p0.locations.get(0).toString()}")
//                    latitudeTxt.text=p0.locations.get(0).latitude.toString()
//                    longtudeTxt.text=p0.locations.get(0).longitude.toString()
                    val geocoder = Geocoder(baseContext, Locale.getDefault())
                    val addresses = geocoder.getFromLocation(
                        p0.locations.get(0).latitude,
                        p0.locations.get(0).longitude,
                        1
                    )
                    if (addresses != null && !addresses.isEmpty()) {
                        val address = addresses[0]
                        val addressText = address.getAddressLine(0) // Full address
                        //addressTxt.text = addressText

                    }


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

