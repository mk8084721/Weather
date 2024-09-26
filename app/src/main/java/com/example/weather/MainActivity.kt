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
    lateinit var navigationView: NavigationView
    lateinit var drawerLayout: DrawerLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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

        val lon = intent.getDoubleExtra("lon", 0.0)
        val lat = intent.getDoubleExtra("lat", 0.0)
        Log.i("TAG", "in MainActivity:\nlon:$lon\nlat$lat")
        // Create a bundle and pass it to HomeFragment
        val bundle = Bundle()/*.apply {
            putFloat("lat", lat.toFloat())  // latitude obtained
            putDouble("lon", lon)  // longitude obtained
        }*/
        val navController = findNavController(this, R.id.nav_host_fragment)
        setupWithNavController(navigationView, navController)
//
//        navController.navigate(R.id.homeFragment, bundle)

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
//        val bundle = Bundle().apply {
//            putDouble("lat", lat)
//            putDouble("lon", longitude)
//        }
//        navController.navigate(R.id.homeFragment, bundle)
        /*if (isFirstTime(this)) {
            showFirstTimeCustomAlert()
            setFirstTimeFlag(this, false)
        }*/
        //showFirstTimeCustomAlert()

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

