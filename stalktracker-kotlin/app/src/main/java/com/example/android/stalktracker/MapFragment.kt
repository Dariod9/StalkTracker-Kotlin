package com.example.android.stalktracker

import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.databinding.DataBindingUtil
import androidx.databinding.DataBindingUtil.setContentView
import androidx.databinding.ViewDataBinding
import com.example.android.stalktracker.databinding.ActivityMapsBinding
import com.example.android.stalktracker.databinding.FragmentMapBinding
import com.example.android.stalktracker.databinding.FragmentProfileBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.internal.ManufacturerUtils
import com.google.firebase.auth.FirebaseAuth
import java.util.jar.Manifest

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MapFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MapFragment : Fragment(), OnMapReadyCallback, LocationListener {

    private lateinit var mMap: GoogleMap
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()
    private lateinit var locationManager: LocationManager
    private lateinit var positions : ArrayList<LatLng>
    var currentLocation : Location? = null
    var fusedLocationProviderClient: FusedLocationProviderClient? = null
    val REQUEST_CODE = 101

    companion object {
        var mapFragment : SupportMapFragment?=null
        val TAG: String = MapFragment::class.java.simpleName
        fun newInstance() = MapFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var rootView = inflater.inflate(R.layout.fragment_map, container, false)

        val act= activity as LoggedActivity
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(act)

        positions=ArrayList()


//        fetchLocation(act)

        getLocation()
        getPositions()


//        Log.println(Log.DEBUG, String(), "LOCATION1 : $currentLocation")
        mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        Thread.sleep(2000)
        mapFragment?.getMapAsync(this)
//        Log.println(Log.DEBUG, String(), "LOCATION2 : $currentLocation")



        return rootView
    }

    private fun getPositions() {
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    Log.println(Log.DEBUG, String(), "Entrou no success")
                    for (document in documents) {
                        if (document.data.get("positions") != "[]") {
                            var tmp = (document.data.get("positions") as List<*>)
                            //                        tmp.filter { it -> (it as String).length>0 }.forEach(positions.add((activity as LoggedActivity).locationParser("$it") )}
                            for (pos in tmp) {
                                Log.println(Log.DEBUG, String(), "Pos:"+pos.toString())
                                positions.add((activity as LoggedActivity).locationParser(pos.toString()))
                            }
                        }
                        else{
                            positions.add(LatLng(0.0,0.0))
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }
    }

    private fun fetchLocation(act : Activity) {
        if(context?.let { ActivityCompat.checkSelfPermission(act, android.Manifest.permission.ACCESS_FINE_LOCATION) } !=
                PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    act, android.Manifest.permission.ACCESS_COARSE_LOCATION)
            }
        != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(act, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), REQUEST_CODE)
            return
        }
        Log.println(Log.DEBUG, String(), "A ir buscar")

        val task = fusedLocationProviderClient?.lastLocation
        task!!.addOnSuccessListener { location ->
            Log.println(Log.DEBUG, String(), "Sucesso")

            if(location != null){

                currentLocation = location

            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {

        var markers=ArrayList<MarkerOptions>()
        Log.println(Log.DEBUG, String(), "Mapa Ready")
        mMap = googleMap!!

//        val lating = LatLng(-34.0, 151.0)
//        val lating = LatLng(currentLocation!!.latitude, currentLocation!!.longitude)
//        val markerOptions = MarkerOptions().position(lating).title("I AM HERE BOYY")
        Log.println(Log.DEBUG, String(), "Mapa foi ready")

        for(pos in positions){
            val markerOptions = MarkerOptions().position(pos).title("TOU AQUI ZÉ")
            mMap.addMarker(markerOptions)
            markers.add(markerOptions)
//            map.animateCamera(CameraUpdateFactory.newLatLng(pos))
//            map.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f))
//            Log.println(Log.DEBUG, String(), "Position: "+pos)

//            markers.add(MarkerOptions().position(pos).title("TOU AQUI ZÉ"))
        }

        val builder = LatLngBounds.Builder()
        for (marker in markers) {
            builder.include(marker.position)
        }
        val bounds = builder.build()
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

//        for(marker in markers) map.addMarker(marker)

        mMap.uiSettings.isMyLocationButtonEnabled = false

//        mMap = googleMap!!
//        // Add a marker in Sydney and move the camera
//        val sydney = LatLng(-34.0, 151.0)
//        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        when(requestCode){
//            REQUEST_CODE -> {
//                if(grantResults.size>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    Log.println(Log.DEBUG, String(), "Fez o fetch")
//                    fetchLocation((activity) as LoggedActivity)
//
//                }
//            }
//        }
//    }

    private fun getLocation() {
        locationManager = (activity as LoggedActivity)!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity as LoggedActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
//        currentLocation=location
        Log.println(Log.DEBUG, String(), "LOCATION : ${location.latitude}")

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.println(Log.DEBUG, String(), "GRANTED")

            }
            else {
                Log.println(Log.DEBUG, String(), "DENIED")

            }
        }
    }

}