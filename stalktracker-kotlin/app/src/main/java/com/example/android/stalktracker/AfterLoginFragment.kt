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

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.stalktracker.databinding.FragmentAfterloginBinding
import com.google.firebase.auth.FirebaseAuth

import android.location.LocationManager
import android.os.Build
import android.os.Parcelable
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

import models.Device
import models.DeviceAdapter
import java.time.LocalDateTime

class AfterLoginFragment : Fragment(), LocationListener {
    private lateinit var auth: FirebaseAuth
    private val adapter = DeviceAdapter()
    private var location : LatLng= LatLng(0.0,0.0)

    private lateinit var locationManager: LocationManager
    private var m_devices: ArrayList<Device> = ArrayList()
    private var friendsAdresses: ArrayList<String> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        auth = FirebaseAuth.getInstance()

        val binding: FragmentAfterloginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_afterlogin, container, false
        )

        val act = activity as LoggedActivity
        binding.foundView.adapter = adapter

//        act.deviceViewModel.allDevices.observe(viewLifecycleOwner, Observer { words ->
//            // Update the cached copy of the words in the adapter.
//            words?.let { adapter.data=it }
//        })

        adapter.setOnItemClickListener(object : DeviceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val item = adapter.data[position]
                adapter.friendAlert(context, act, item)
            }

        })

        //LOCATION

        val locationManager =
            context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
        val isGpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!isGpsEnabled) {
            startActivityForResult(
                Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS),
                2
            )
        }


        getLocation()


        //BT

        val bluetoothManager =
            context?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val m_bluetoothAdapter = bluetoothManager.getAdapter()
        val filter = IntentFilter()

        filter.addAction(BluetoothDevice.ACTION_FOUND)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)


        binding.textView3.setText(auth.currentUser?.email)

        binding.searchButton.setOnClickListener {
            Log.println(Log.DEBUG, String(), "Carregou")
            m_devices.clear()

            if (!m_bluetoothAdapter!!.isEnabled) {
                val enableBT = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBT, 1)
            } else {
                val toast = Toast.makeText(context, "Please Wait", Toast.LENGTH_LONG)
                toast.show()

                getFriends()
                Log.println(Log.DEBUG, String(), friendsAdresses.size.toString());
                Log.println(Log.DEBUG, String(), m_bluetoothAdapter.startDiscovery().toString());
                activity?.registerReceiver(mReceiver, filter);
            }
        }

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.options_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    fun getFriends() {

        val devices = FirebaseUtils().fireStoreDatabase.collection("Users")
            .document("dcmatos.99@gmail.com")
            .collection("users")
            .whereEqualTo("friend", true)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    friendsAdresses.add((document.data.get("address").toString()))
                    Log.println(Log.DEBUG, String(), "É ISTO:")
                    Log.println(
                        Log.DEBUG,
                        String(),
                        "É ISTO: " + "${document.id} => ${document.data}"
                    )
                }
            }
            .addOnFailureListener { exception ->
                Log.println(Log.DEBUG, String(), "ERRO")
            }
    }


    private fun getLocation() {
        locationManager = (activity as LoggedActivity)!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(activity as LoggedActivity, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 2)
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
//        currentLocation=location
        this.location=LatLng(location.latitude, location.longitude)
        Log.println(Log.DEBUG, String(), "LOCATION : ${location.latitude} , ${location.longitude}")

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

    private val mReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("MissingPermission")
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED == action) {
                Log.println(Log.DEBUG, String(), "Started")
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED == action) {
                Log.println(Log.DEBUG, String(), "Finished")
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND == action) {
                //bluetooth device found
                val device =
                    intent.getParcelableExtra<Parcelable>(BluetoothDevice.EXTRA_DEVICE) as BluetoothDevice?
                if (device != null) {
                    val newdev :Device
                    if(device.name!=null)
                        newdev = Device(device.name, device.address, false, false, location)
                    else newdev = Device(device.address,device.address, false,false,location)
                    if (!friendsAdresses.contains(newdev.address)) {
                        m_devices.add(newdev)
                        auth.currentUser?.email?.let {
                            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                                .collection("users")
                                .document(newdev.address)
                                .set(newdev)
                                .addOnSuccessListener {
                                    Log.println(Log.DEBUG, String(), "Added document ")
                                }
                                .addOnFailureListener { exception ->
                                    Log.println(Log.DEBUG, String(), "Error adding document")
                                }
                        }

                    }
                }
                adapter.data = m_devices

                Log.println(Log.DEBUG, String(), "Found device " + adapter.data.size.toString())
            }
        }
    }

}