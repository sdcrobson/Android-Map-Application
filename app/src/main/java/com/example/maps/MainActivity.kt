package com.example.maps


import android.annotation.SuppressLint
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.mapbox.android.core.location.LocationEngine
import com.mapbox.android.core.permissions.PermissionsListener
import com.mapbox.android.core.permissions.PermissionsManager
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.MarkerOptions
import com.mapbox.mapboxsdk.camera.CameraPosition
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions
import com.mapbox.mapboxsdk.location.LocationComponentOptions
import com.mapbox.mapboxsdk.location.modes.CameraMode
import com.mapbox.mapboxsdk.location.modes.RenderMode
import com.mapbox.mapboxsdk.maps.MapView
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style


class MainActivity : AppCompatActivity(), OnMapReadyCallback, PermissionsListener {

    private var permissionsManager: PermissionsManager = PermissionsManager(this)
    private lateinit var mapboxMap: MapboxMap

    //private var lat: Double = 0.0
    //private var lng: Double = 0.0


    private var mapView: MapView? = null

    private lateinit var map: MapboxMap
    private lateinit var permissionManager: PermissionsManager
    private lateinit var originalLocation: Location

    private var locationEngine: LocationEngine? = null
    //private var locationLayerPlugin: LocationLayerPlugin = null


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.mapbox_access_token))

        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.myToolbar))
        mapView = findViewById(R.id.mapView)

        mapView?.onCreate(savedInstanceState)
        mapView?.getMapAsync { mapboxMap ->

            mapboxMap.setStyle(Style.MAPBOX_STREETS) {
            }
        }

    }
    override fun onMapReady(mapboxMap: MapboxMap) {
        this.mapboxMap = mapboxMap

        mapboxMap.setStyle(
            Style.Builder().fromUri(
                "mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"
            )
        ) {

        // Map is set up and the style has loaded. Now you can add data or make other map adjustments
            //lat =  mapboxMap.locationComponent.lastKnownLocation!!.latitude
            //lng = mapboxMap.locationComponent.lastKnownLocation!!.longitude

            enableLocationComponent(it)
        }
    }
    @SuppressLint("MissingPermission")
    private fun enableLocationComponent(loadedMapStyle: Style) {
            // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {

            // Create and customize the LocationComponent's options
            val customLocationComponentOptions = LocationComponentOptions.builder(this)
                .trackingGesturesManagement(true).build()

            val locationComponentActivationOptions = LocationComponentActivationOptions.builder(
                this,
                loadedMapStyle
            )
                .locationComponentOptions(customLocationComponentOptions).build()

            // Get an instance of the LocationComponent and then adjust its settings
            mapboxMap.locationComponent.apply {

                // Activate the LocationComponent with options
                activateLocationComponent(locationComponentActivationOptions)

            // Enable to make the LocationComponent visible
                isLocationComponentEnabled = true

            // Set the LocationComponent's camera mode
                cameraMode = CameraMode.TRACKING

            // Set the LocationComponent's render mode
                renderMode = RenderMode.COMPASS
            }
        } else {
            permissionsManager = PermissionsManager(this)
            permissionsManager.requestLocationPermissions(this)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
    override fun onExplanationNeeded(permissionsToExplain: List<String>) {
        Toast.makeText(this, "User Location Permission Explanation", Toast.LENGTH_LONG).show()
    }
    override fun onPermissionResult(granted: Boolean) {
        if (granted) {
            enableLocationComponent(mapboxMap.style!!)
        } else {
            Toast.makeText(this, "User Location Permission Not Granted", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.appbar, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //return super.onOptionsItemSelected(item);
        var returnVal = false
        when (item.itemId) {
            R.id.action_satellite_view -> {
                mapView?.getMapAsync { mapboxMap ->
                    mapboxMap.setStyle(Style.SATELLITE) {
                        returnVal = true
                    }
                }
            }
            R.id.action_dark_view -> {
                mapView?.getMapAsync { mapboxMap ->
                    mapboxMap.setStyle(Style.DARK) {
                        returnVal = true
                    }
                }
            }
            R.id.action_traffic_day -> {
                mapView?.getMapAsync { mapboxMap ->
                    mapboxMap.setStyle(Style.TRAFFIC_DAY) {
                        returnVal = true
                    }
                }
            }
            R.id.action_current_location -> {
                mapView?.getMapAsync { mapboxMap ->
                    onMapReady(mapboxMap)
                }
            }
            R.id.action__pyramids -> {
                mapView?.getMapAsync { mapboxMap ->
                    val data = ArrayList<FaveLocation>()
                    data.add(FaveLocation(LatLng(29.978988, 31.134320), "Pyramid of Giyza"))
                    for (items in data) {
                        mapboxMap.addMarker(
                            MarkerOptions().position(items.getLatLng).title(items.getTitle)
                        )
                        val position = CameraPosition.Builder()
                            .target(LatLng(items.getLatLng)) // Sets the new camera position
                            .zoom(17.0) // Sets the zoom
                            .bearing(180.0) // Rotate the camera
                            .tilt(30.0) // Set the camera tilt
                            .build() // Creates a CameraPosition from the builder

                        mapboxMap.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(position), 7000
                        )
                    }
                }
            }
            R.id.action_area_51 -> {
                mapView?.getMapAsync { mapboxMap ->
                    val data = ArrayList<FaveLocation>()
                    data.add(FaveLocation(LatLng(37.233429, -115.804524), "Area 51"))
                    for (items in data) {
                        mapboxMap.addMarker(
                            MarkerOptions().position(items.getLatLng).title(items.getTitle)
                        )
                        val position = CameraPosition.Builder()
                            .target(LatLng(items.getLatLng)) // Sets the new camera position
                            .zoom(17.0) // Sets the zoom
                            .bearing(180.0) // Rotate the camera
                            .tilt(30.0) // Set the camera tilt
                            .build() // Creates a CameraPosition from the builder

                        mapboxMap.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(position), 7000
                        )
                    }
                }
            }
            R.id.action_Mount_Robson -> {
                mapView?.getMapAsync { mapboxMap ->
                    val data = ArrayList<FaveLocation>()
                    data.add(FaveLocation(LatLng(53.116700, -119.150000), "Mount Robson"))
                    for(items in data){
                        mapboxMap.addMarker(MarkerOptions().position(items.getLatLng).title(items.getTitle))
                        val position = CameraPosition.Builder()
                            .target(LatLng(items.getLatLng)) // Sets the new camera position
                            .zoom(17.0) // Sets the zoom
                            .bearing(180.0) // Rotate the camera
                            .tilt(30.0) // Set the camera tilt
                            .build() // Creates a CameraPosition from the builder
                            mapboxMap.animateCamera(
                            CameraUpdateFactory
                                .newCameraPosition(position), 7000
                            )
                    }
                }
                        }
                    }
        return returnVal
    }
}



/*val position = CameraPosition.Builder()
                    .target(LatLng(51.50550, -0.07520))
                    .zoom(10.0)
                    .tilt(20.0)
                    .build()
                mapboxMap.animateCamera(
                    CameraUpdateFactory
                        .newCameraPosition(position), 7000
                )
                */

