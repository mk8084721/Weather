package com.example.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation.findNavController
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.weather.databinding.ActivityMainBinding
import com.example.weather.databinding.FirstTimeAlertBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.material.navigation.NavigationView
import java.util.Locale

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var alertBinding: FirstTimeAlertBinding
    private lateinit var fusedClient : FusedLocationProviderClient
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        fusedClient = LocationServices.getFusedLocationProviderClient(this)
        navigationView = binding.bottomNavView
        val actionBar = supportActionBar
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#212121")))
            actionBar.setDisplayShowHomeEnabled(true)
            actionBar.setDisplayShowCustomEnabled(true)
            actionBar.elevation = 0f


            // Inflate the custom action bar layout
            val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val customView = inflater.inflate(R.layout.custom_action_bar, null)

            // Use ActionBar.LayoutParams to make the custom view fill the entire width
            val layoutParams = ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
            )
            actionBar.setCustomView(customView, layoutParams)

            // Hide the default title
            actionBar.setDisplayShowTitleEnabled(false)

            // Get the menu icon from the custom view
            val menuIcon = customView.findViewById<ImageView>(R.id.menu_icon)

            // Set click listener for the menu icon
            menuIcon.setOnClickListener {
                // Handle the click, e.g., open the drawer or trigger an action
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
            }
        }
        drawerLayout = binding.main
        val navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val customView = actionBar?.customView
            val titleTextView = customView?.findViewById<TextView>(R.id.action_bar_title)

            // Set the Action Bar title based on the fragment
            when (destination.id) {
                R.id.homeFragment -> titleTextView?.text = "Home"
                R.id.favoriteFragment -> titleTextView?.text = "Favorite"
                R.id.alertsFragment -> titleTextView?.text = "Alerts"
                R.id.settingsFragment -> titleTextView?.text = "Settings"
                // Add more cases for other fragments
                else -> titleTextView?.text = "Weather App"
            }
        }
        /*if (isFirstTime(this)) {
            showFirstTimeCustomAlert()
            setFirstTimeFlag(this, false)
        }*/
        //showFirstTimeCustomAlert()

    }

    /*override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START)
            } else {
                drawerLayout.openDrawer(GravityCompat.START)
            }
        }

        return super.onOptionsItemSelected(item)
    }*/

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

