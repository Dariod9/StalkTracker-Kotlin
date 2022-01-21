/*
 * Copyright 2018, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.stalktracker

import android.opengl.Visibility
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import com.example.android.stalktracker.databinding.FragmentProfileBinding
import com.example.android.stalktracker.databinding.FragmentProfileBindingImpl
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import models.Device
import models.DeviceAdapter
import java.text.SimpleDateFormat
import java.util.*

import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.Marker
import com.google.firebase.auth.FirebaseAuth
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.LatLngBounds





class ProfileFragment : Fragment(), OnMapReadyCallback {
    private val adapter = DeviceAdapter()
    private lateinit var mapView : MapView
    private lateinit var map : GoogleMap
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var position : LatLng = LatLng(0.0,0.0)
    private lateinit var positions : ArrayList<LatLng>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

//        val listaPos=ArrayList<LatLng>()
//        listaPos.add(LatLng(arguments?.getDouble("latitude")!!,arguments?.getDouble("longitude")!!))
        positions= ArrayList()

        val device= Device(arguments?.getString("name").toString(),
                            arguments?.getString("address").toString(),
                            arguments?.getString("friend").toBoolean(),
                            arguments?.getString("stalker").toBoolean(),
                            arguments?.getParcelableArrayList("positions")!!)


        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("address", device.address)
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
                            positions.add(position)
                        }

                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }

        Log.println(Log.DEBUG, String(), device.positions.toString())

        binding.name.setText(device.name)
        binding.mac.setText(device.address)
        if(device.friend) binding.friend.setText(" Friend!") else binding.friend.setText(" Not a Friend")
        if(device.black) binding.stalker.setText(" Stalker!") else binding.stalker.setText(" Not a Stalker")

        if(!device.friend) binding.rmFriend.visibility=View.INVISIBLE
        if(!device.black) binding.rmStalker.visibility=View.INVISIBLE

        binding.rmStalker.setOnClickListener {
            val act = activity as LoggedActivity
            adapter.removeStalkerAlert(context, act, device)
        }

        binding.rmFriend.setOnClickListener {
            val act = activity as LoggedActivity
            adapter.removeFriendAlert(context, act, device)
        }



        mapView = binding.mapView as MapView
        mapView.onCreate(savedInstanceState)

        Thread.sleep(2000)
        mapView.getMapAsync(this)

//        map = mapView.map;
//        map.getUiSettings().setMyLocationButtonEnabled(false);
//        map.setMyLocationEnabled(true);

//        val mapFragment = (activity as LoggedActivity).supportFragmentManager.findFragmentById(R.id.mapView) as SupportMapFragment
//        mapFragment.getMapAsync(this)
//
//        val mapFragment2 = SupportMapFragment.newInstance()
//        (activity as LoggedActivity).supportFragmentManager
//            .beginTransaction()
//            .add(R.id.mapView, mapFragment)
//            .commit()


        return binding.root
    }

    override fun onMapReady(p0: GoogleMap?) {
        val markers=ArrayList<MarkerOptions>()

        Log.println(Log.DEBUG, String(), "Positions: "+positions)

        map = p0!!
//        val markerOptions = MarkerOptions().position(position).title("TOU AQUI ZÉ")
        for(pos in positions){
            val markerOptions = MarkerOptions().position(pos).title("TOU AQUI ZÉ")
            map.addMarker(markerOptions)
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
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))

//        for(marker in markers) map.addMarker(marker)

        map.uiSettings.isMyLocationButtonEnabled = false
//        map.isMyLocationEnabled = true
        /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/
        /*
       //in old Api Needs to call MapsInitializer before doing any CameraUpdateFactory call
        try {
            MapsInitializer.initialize(this.getActivity());
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
       */

        // Updates the location and zoom of the MapView
        /*CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(43.1, -87.9), 10);
        map.animateCamera(cameraUpdate);*/


    }

    override fun onResume() {
        mapView.onResume()
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }
}
