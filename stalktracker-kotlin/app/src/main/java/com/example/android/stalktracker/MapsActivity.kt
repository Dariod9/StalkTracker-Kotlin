//package com.example.android.stalktracker
//
//import android.content.Context
//import android.content.Intent
//import android.location.Location
//import android.location.LocationListener
//import android.location.LocationManager
//import androidx.appcompat.app.AppCompatActivity
//import android.os.Bundle
//import android.provider.Settings
//
//import com.google.android.gms.maps.CameraUpdateFactory
//import com.google.android.gms.maps.GoogleMap
//import com.google.android.gms.maps.OnMapReadyCallback
//import com.google.android.gms.maps.SupportMapFragment
//import com.google.android.gms.maps.model.LatLng
//import com.google.android.gms.maps.model.MarkerOptions
//import com.example.android.stalktracker.databinding.ActivityMapsBinding
//import com.google.firebase.ktx.Firebase
//
//class MapsActivity : AppCompatActivity(), OnMapReadyCallback {
//
//    private lateinit var map: GoogleMap
//    private lateinit var binding: ActivityMapsBinding
//    lateinit var locationManager: LocationManager
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMapsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
////        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,2000,200,locationlistener)
//
//        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
//        val mapFragment = supportFragmentManager
//            .findFragmentById(R.id.map) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//    }
//
//    /**
//     * Manipulates the map once available.
//     * This callback is triggered when the map is ready to be used.
//     * This is where we can add markers or lines, add listeners or move the camera. In this case,
//     * we just add a marker near Sydney, Australia.
//     * If Google Play services is not installed on the device, the user will be prompted to install
//     * it inside the SupportMapFragment. This method will only be triggered once the user has
//     * installed Google Play services and returned to the app.
//     */
//    override fun onMapReady(googleMap: GoogleMap) {
//        map = googleMap
//
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        map.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        map.moveCamera(CameraUpdateFactory.newLatLng(sydney))
//    }
//
////    private fun getLocation() {
////        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
////        val hasGps = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
////        val hasNetwork = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
////        if (hasGps || hasNetwork) {
////
////            if (hasGps) {
////                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0F, object :
////                    LocationListener {
////                    override fun onLocationChanged(p0: Location) {
////                        if (p0 != null) {
////                            locationGps = p0
////
////                        }
////                    }
////
////                })
////
////                val localGpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
////                if (localGpsLocation != null)
////                    locationGps = localGpsLocation
////            }
////            if (hasNetwork) {
////                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0F, object : LocationListener {
////                    override fun onLocationChanged(p0: Location) {
////                        if (p0 != null) {
////                            locationNetwork = p0
////                        }
////                    }
////
////                })
////
////                val localNetworkLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
////                if (localNetworkLocation != null)
////                    locationNetwork = localNetworkLocation
////            }
////
////            if(locationGps!= null && locationNetwork!= null){
////                if(locationGps!!.accuracy > locationNetwork!!.accuracy){
////                }else{
////
////                }
////            }
////
////        } else {
////            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
////        }
////    }
//}