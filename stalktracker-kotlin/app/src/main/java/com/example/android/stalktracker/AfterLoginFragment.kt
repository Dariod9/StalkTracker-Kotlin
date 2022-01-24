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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.*
import android.content.Context.NOTIFICATION_SERVICE
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricManager
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
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.navigation.NavDeepLink
import androidx.navigation.NavDeepLinkBuilder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.maps.model.LatLng

import models.Device
import models.DeviceAdapter
import java.text.DecimalFormat
import java.time.LocalDateTime

class AfterLoginFragment : Fragment(), LocationListener {
    private lateinit var auth: FirebaseAuth
    private val adapter = DeviceAdapter()
    private var location: LatLng = LatLng(0.0, 0.0)
//    private var positions: ArrayList<LatLng> = ArrayList()

    private lateinit var locationManager: LocationManager
    private var m_devices: ArrayList<Device> = ArrayList()
    private var addresses: ArrayList<String> = ArrayList()
    private var friendsAdresses: ArrayList<String> = ArrayList()
    private var notification_id = 0


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
        getFriends()


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
            addresses= ArrayList()
            Log.println(Log.DEBUG, String(), "Addresses:"+addresses);

            adapter.data=ArrayList()
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

        createNotificationChannel()

        setHasOptionsMenu(true)

        return binding.root
    }

//    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
//        super.onCreateOptionsMenu(menu, inflater)
//        inflater.inflate(R.menu.options_menu, menu)
//    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return NavigationUI.onNavDestinationSelected(item, requireView().findNavController())
                || super.onOptionsItemSelected(item)
    }

    fun getFriends() {

        auth.currentUser?.email?.let {

            friendsAdresses = ArrayList()
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("friend", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        friendsAdresses.add((document.data.get("address").toString()))
                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }


            FirebaseUtils().fireStoreDatabase.collection("Users")

        }

    }


    private fun getLocation() {
        locationManager =
            (activity as LoggedActivity)!!.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if ((ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED)
        ) {
            ActivityCompat.requestPermissions(
                activity as LoggedActivity,
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                2
            )
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 5f, this)
    }

    override fun onLocationChanged(location: Location) {
//        currentLocation=location
        this.location = LatLng(location.latitude, location.longitude)
        Log.println(Log.DEBUG, String(), "LOCATION : ${location.latitude} , ${location.longitude}")

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 2) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.println(Log.DEBUG, String(), "GRANTED")

            } else {
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
//                    val newdev: Device
                    var name: String
                    if (device.name != null) name = device.name
//
//                        positions.clear()
//                        positions.add(location)
//                        newdev = Device(device.name, device.address, false, false, positions)}
                    else name = device.address
                    checkStalker(device.address, name)
//                    newdev = Device(device.address, device.address, false, false, positions)
                    if (!friendsAdresses.contains(device.address) && (!addresses.contains(device.address))) {

                        Log.println(Log.DEBUG, String(), "Endereço: " + device.address)
                        Log.println(Log.DEBUG, String(), "Friends:: " + friendsAdresses)
                        Log.println(Log.DEBUG, String(), "Addresses:: " + addresses)

                        m_devices.add(Device(name, device.address, false, false))
                        addresses.add(device.address)
                        insertDevice(name, device.address)
//                        Log.println(Log.DEBUG, String(), "Devices : "+ m_devices)

//                        m_devices.add(newdev)
//                        Log.println(Log.DEBUG, String(), newdev.toString())
//                        auth.currentUser?.email?.let {
//                            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
//                                .collection("users")
//                                .document(device.address)
//                                .set(newdev)
//                                .addOnSuccessListener {
//                                    Log.println(Log.DEBUG, String(), "Added document ")
//                                }
//                                .addOnFailureListener { exception ->
//                                    Log.println(Log.DEBUG, String(), "Error adding document")
//                                }
//                        }

                    }
                }
                adapter.data = m_devices

                Log.println(Log.DEBUG, String(), "Found device " + adapter.data.size.toString())
            }
        }
    }

    private fun checkStalker(address: String?, name : String?) {
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("black", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if(document.data["address"].toString()==address){
                            adapter.stalkerAlert(context, (activity as LoggedActivity))
                            createNotification(name!!)
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification Channel"
            val description = "A description of the channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("channel2", name, importance)
            channel.description = description

            // Register the channel with the system
            val notificationManager = (activity as LoggedActivity).getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun createNotification(address: String){
        // Create an explicit intent for an Activity in your app
        val intent = Intent(context, LoggedActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = context?.let {
            NotificationCompat.Builder(it, channelID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Stalker found!")
                .setContentText(address)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
        }

        if (builder != null) {
            with(context?.let { NotificationManagerCompat.from(it) }) {
                // notificationId is a unique int for each notification that you must define
                this?.notify(notification_id, builder.build())
                notification_id += 1
            }
        }

    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun insertDevice(name: String, address: String) {
        var positions = ArrayList<LatLng>()
        var device: Device


        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("address", address)
                .get()
                .addOnSuccessListener { documents ->
                    Log.println(Log.DEBUG, String(), "Endereço procurado: " + address)
                    Log.println(Log.DEBUG, String(), "Lista: " + documents.size())

                    if (documents.size() > 0) {
                        for (document in documents) {
                            Log.println(Log.DEBUG, String(), "Device encontrado: " + document.data)

                            if (document.data.get("positions") != null) {
                                var tmp = (document.data.get("positions") as List<*>)
                                //                        tmp.filter { it -> (it as String).length>0 }.forEach(positions.add((activity as LoggedActivity).locationParser("$it") )}
                                for (pos in tmp) {
                                    Log.println(Log.DEBUG, String(), "Pos:"+pos.toString())
                                    positions.add((activity as LoggedActivity).locationParser(pos.toString()))
                                }
                            }
                            if(location.latitude!=0.0 && location.longitude!=0.0)positions.add(location)
                            Log.println(Log.DEBUG, String(), positions.toString())

                            device = Device(
                                document.data["name"].toString(),
                                document.data["address"].toString(),
                                document.data["friend"].toString().toBoolean(),
                                document.data["stalker"].toString().toBoolean(),
                                positions, positions.size
                            )

                            m_devices.add(device)

                            auth.currentUser?.email?.let {
                                FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                                    .collection("users")
                                    .document(address)
                                    .update("positions", positions, "npos", positions.size)
                                    .addOnSuccessListener {
                                        Log.println(Log.DEBUG, String(), "Added document ")
                                    }
                                    .addOnFailureListener { exception ->
                                        Log.println(Log.DEBUG, String(), "Error adding document")
                                    }
                            }
                            //                        positions.add()
                            //                            Log.println(
                            //                                Log.DEBUG,
                            //                                String(),
                            //                                (document.data.get("positions") as List<*>)[0].toString()
                            //                            )
                            //                        friendsAdresses.add((document.data.get("address").toString()))
                            //                        Log.println(Log.DEBUG, String(), "É ISTO:")
                            //                        Log.println(
                            //                            Log.DEBUG,
                            //                            String(),
                            //                            "É ISTO: " + "${document.id} => ${document.data}"
                            //                        )
                        }
                    } else {
                        if(location.latitude!=0.0 && location.longitude!=0.0)positions.add(location)

                        device = Device(
                            name,
                            address,
                            false,
                            false,
                            positions, positions.size
                        )

//                        m_devices.add(device)

                        auth.currentUser?.email?.let {
                            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                                .collection("users")
                                .document(device.address)
                                .set(device)
                                .addOnSuccessListener {

                                    Log.println(Log.DEBUG, String(), "Added document ")
                                }
                                .addOnFailureListener { exception ->
                                    Log.println(Log.DEBUG, String(), "Error adding document")
                                }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }
//        Thread.sleep(2000)
//        adapter.data=m_devices
//        Log.println(Log.DEBUG, String(), "DEvices : "+ m_devices)
    }

}