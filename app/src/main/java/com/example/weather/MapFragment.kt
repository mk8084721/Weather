package com.example.weather

import android.content.Context
import android.content.Intent
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.weather.Favorite.viewModel.FavoriteViewModel
import com.example.weather.Favorite.viewModel.FavoriteViewModelFactory
import com.example.weather.Repo.WeatherRepo
import com.example.weather.database.LocalDataSource
import com.example.weather.database.model.Favorite
import com.example.weather.network.API
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay
import java.util.Locale

class MapFragment : Fragment() {

    private lateinit var mapView: MapView
    private lateinit var favViewModel : FavoriteViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val favoriteViewModelFactory = FavoriteViewModelFactory(WeatherRepo(LocalDataSource(requireContext()) , API.retrofitService))
        favViewModel = ViewModelProvider(this , favoriteViewModelFactory).get(FavoriteViewModel::class.java)

        val args: MapFragmentArgs by navArgs()

        // Initialize the MapView
        mapView = view.findViewById(R.id.mapView)

        // Configure the MapView
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osm_pref", 0))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Set the default zoom level and position
        val startPoint = GeoPoint(30.7238221, 30.7340145) // Example: Eiffel Tower
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(startPoint)


        // Add an overlay to detect map clicks
        val mapEventsOverlay = object : Overlay() {
            override fun draw(canvas: android.graphics.Canvas?, mapView: MapView?, shadow: Boolean) {
                // Nothing to draw
            }

            override fun onSingleTapConfirmed(e: android.view.MotionEvent?, mapView: MapView?): Boolean {
                // Get the clicked position on the map
                val projection = mapView?.projection
                val geoPoint = projection?.fromPixels(e?.x?.toInt() ?: 0, e?.y?.toInt() ?: 0) as GeoPoint

                Log.i("MapClick", "Latitude: ${geoPoint.latitude}, Longitude: ${geoPoint.longitude}")

                // Add a marker at the selected position
                val marker = Marker(mapView)
                marker.position = geoPoint
                marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                mapView.overlays.clear() // Clear previous markers
                mapView.overlays.add(marker)
                mapView.invalidate()
                Log.i("TAG", "Map:\nlon: ${geoPoint.longitude}\n${geoPoint.latitude} ")
                // Send the latitude and longitude to another activity
                if (args.page.isNullOrBlank()) {
                    val intent = Intent(activity, MainActivity::class.java).apply {
                        putExtra("lat", geoPoint.latitude)
                        putExtra("lon", geoPoint.longitude)
                    }
                    startActivity(intent)
                }else if(args.page == "fav"){
                    val geocoder = Geocoder(requireContext(), Locale.getDefault())
                    var city = "Unknown City"
                    val addresses = geocoder.getFromLocation(geoPoint.latitude, geoPoint.longitude, 1)
                    if (addresses != null && !addresses.isEmpty()) {
                        val address = addresses[0]
                        city = address.locality ?: "Unknown City"
                    }
                    favViewModel.insertFavWeather(Favorite(geoPoint.longitude , geoPoint.latitude , city))
                    findNavController().navigate(R.id.action_mapFragment_to_favoriteFragment)
                }else if(args.page == "set"){
                    val sharedPreferences = requireContext().getSharedPreferences("LocationPrefs", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    // Converting double to float
                    editor.putFloat("lat", geoPoint.latitude.toFloat())
                    editor.putFloat("lon", geoPoint.longitude.toFloat())
                    editor.apply()
                    val action = MapFragmentDirections.actionMapFragmentToSettingsFragment()
                    // Navigate to MapFragment with the argument
                    findNavController().navigate(action)
                }

                return true // Event is consumed
            }
        }

        // Add the overlay to the map
        mapView.overlays.add(mapEventsOverlay)
        /*// Set a map click listener
        mapView.setOnClickListener { event ->
            // Get the clicked position on the map
            val projection = mapView.projection
            val geoPoint = projection.fromPixels(event.x.toInt(), event.y.toInt())
            Log.i("TAG", "Map OnClickListener: \n${geoPoint.latitude}\n${geoPoint.longitude}")
            // Add a marker at the selected position
            val marker = Marker(mapView)
            marker.position = geoPoint as GeoPoint?
            marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            mapView.overlays.clear() // Clear previous markers
            mapView.overlays.add(marker)
            mapView.invalidate()

            // Send the latitude and longitude to another activity
            val intent = Intent(activity, MainActivity::class.java).apply {
                putExtra("LATITUDE", geoPoint.latitude)
                putExtra("LONGITUDE", geoPoint.longitude)
            }
            startActivity(intent)
        }*/

        return view
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Needed for OSMdroid to work properly
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Needed for OSMdroid to work properly
    }
}
