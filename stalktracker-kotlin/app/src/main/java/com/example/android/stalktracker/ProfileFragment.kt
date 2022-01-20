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
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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







class ProfileFragment : Fragment(), OnMapReadyCallback {
    private val adapter = DeviceAdapter()
    private lateinit var mapView : MapView
    private lateinit var map : GoogleMap
    private lateinit var position : LatLng

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

        val device= Device(arguments?.getString("name").toString(),
                            arguments?.getString("address").toString(),
                            arguments?.getString("friend").toBoolean(),
                            arguments?.getString("stalker").toBoolean(),
                            LatLng(arguments?.getDouble("latitude")!!,arguments?.getDouble("longitude")!!)
        )
        Log.println(Log.DEBUG, String(), device.position.latitude.toString())
        position=device.position

        binding.name.setText(device.name)
        binding.mac.setText(device.address)
        binding.friend.setText(device.friend.toString())
        binding.stalker.setText(device.black.toString())

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
        map = p0!!
        val markerOptions = MarkerOptions().position(position).title("TOU AQUI ZÉ")
        map.addMarker(markerOptions)

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
        map.animateCamera(CameraUpdateFactory.newLatLng(position))
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))

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
