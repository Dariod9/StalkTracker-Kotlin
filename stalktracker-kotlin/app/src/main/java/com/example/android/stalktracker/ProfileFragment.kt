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

import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentProfileBinding
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import models.Device
import models.DeviceAdapter

import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.firebase.auth.FirebaseAuth
import kotlin.collections.ArrayList
import com.google.android.gms.maps.model.LatLngBounds
import models.FirebaseUtils


class ProfileFragment : Fragment(), OnMapReadyCallback {
    private val adapter = DeviceAdapter()
    private var name : String = ""
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


        positions= ArrayList()

        val device= Device(arguments?.getString("name").toString(),
                            arguments?.getString("address").toString(),
                            arguments?.getString("friend").toBoolean(),
                            arguments?.getString("stalker").toBoolean())

        name=device.name

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
        binding.name.setOnClickListener{
            binding.name.setCursorVisible(true);
            binding.name.setFocusableInTouchMode(true);
            binding.name.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.name.requestFocus();
            binding.button4.visibility=View.VISIBLE//to trigger the soft input
        }

        binding.button4.setOnClickListener{
            binding.name.setCursorVisible(false);
            binding.name.setFocusableInTouchMode(false);
            auth.currentUser?.email?.let {
                FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                    .collection("users")
                    .document(device.address)
                    .update("name", binding.name.text.toString())
                    .addOnSuccessListener {
                        Log.println(Log.DEBUG, String(), "Success")
                    }
                    .addOnFailureListener { exception ->
                        Log.println(Log.DEBUG, String(), "Error!")
                    }
            }
            binding.button4.visibility=View.GONE


        }

        binding.mac.setText(device.address)
        if(device.friend) binding.friend.setText(" Friend!") else binding.friend.setText(" Not a Friend")
        if(device.black) binding.stalker.setText(" Stalker!") else binding.stalker.setText(" Not a Stalker")

        if(!device.friend) binding.rmFriend.visibility=View.INVISIBLE
        if(!device.black) binding.rmStalker.visibility=View.INVISIBLE

        binding.rmStalker.setOnClickListener {
            val act = activity as LoggedActivity
            if(adapter.removeStalkerAlert(context, act, device))
                view?.findNavController()?.navigate(R.id.action_profileFragment_to_afterLoginFragment)

        }

        binding.rmFriend.setOnClickListener {
            val act = activity as LoggedActivity
            adapter.removeFriendAlert(context, act, device)
        }





        mapView = binding.mapView as MapView
        mapView.onCreate(savedInstanceState)

        Thread.sleep(2000)
        mapView.getMapAsync(this)


        return binding.root
    }

    override fun onMapReady(p0: GoogleMap) {
        val markers=ArrayList<MarkerOptions>()

        Log.println(Log.DEBUG, String(), "Positions: "+positions)

        map = p0!!
        if(positions.size!=0) {
            for(pos in positions) {
                val markerOptions = MarkerOptions().position(pos).title(name)
                map.addMarker(markerOptions)
                markers.add(markerOptions)

            }
        }
        else{
            val markerOptions = MarkerOptions().position(LatLng(0.0,0.0)).title("")
            map.addMarker(markerOptions)
            markers.add(markerOptions)
        }
        val builder = LatLngBounds.Builder()
        for (marker in markers) {
            builder.include(marker.position)
        }
        val bounds = builder.build()
        map.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100))


        map.uiSettings.isMyLocationButtonEnabled = false


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
