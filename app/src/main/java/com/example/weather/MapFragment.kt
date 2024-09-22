package com.example.weather

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import org.osmdroid.config.Configuration
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Overlay

class MapFragment : Fragment() {

    private lateinit var mapView: MapView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        // Initialize the MapView
        mapView = view.findViewById(R.id.mapView)

        // Configure the MapView
        Configuration.getInstance().load(requireContext(), requireContext().getSharedPreferences("osm_pref", 0))
        mapView.setTileSource(TileSourceFactory.MAPNIK)
        mapView.setBuiltInZoomControls(true)
        mapView.setMultiTouchControls(true)

        // Set the default zoom level and position
        val startPoint = GeoPoint(48.8583, 2.2944) // Example: Eiffel Tower
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

                // Send the latitude and longitude to another activity
                val intent = Intent(activity, MainActivity::class.java).apply {
                    putExtra("LATITUDE", geoPoint.latitude)
                    putExtra("LONGITUDE", geoPoint.longitude)
                }
                startActivity(intent)

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
