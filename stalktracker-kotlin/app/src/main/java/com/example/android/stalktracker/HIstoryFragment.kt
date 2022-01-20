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

import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentHistoryBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import models.Device
import models.DeviceAdapter
import models.DeviceDate
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList

class HIstoryFragment : Fragment() {

    private val adapter = DeviceAdapter()
    private var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var dayDevices: ArrayList<Device> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentHistoryBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_history, container, false
        )


        val date = binding.date
        val button = binding.button2
        binding.viewHistory.adapter = adapter



        adapter.setOnItemClickListener(object : DeviceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val item = adapter.data[position]
//                adapter.removeFriendAlert(context, act, item
                Log.println(Log.DEBUG, String(), item.position.latitude.toString())
                val bundle = bundleOf(
                    "name" to item.name,
                    "address" to item.address,
                    "friend" to item.friend.toString(),
                    "stalker" to item.black.toString(),
                    "latitude" to item.position.latitude,
                    "longitude" to item.position.longitude
                )
                view?.findNavController()
                    ?.navigate(R.id.action_historyFragment_to_profileFragment, bundle)

            }

        })


        val myCalendar = Calendar.getInstance()

        val today = DeviceDate(
            LocalDateTime.now().dayOfMonth,
            LocalDateTime.now().monthValue,
            LocalDateTime.now().month.toString(),
            LocalDateTime.now().year
        )

        getDates(today, adapter)


        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            myCalendar.set(Calendar.YEAR, year)
            myCalendar.set(Calendar.MONTH, month)
            myCalendar.set(Calendar.DAY_OF_MONTH, day)
            updateLabel(myCalendar, date)
            getDates(DeviceDate(day, month+1, "", year), adapter)

        }

        button.setOnClickListener {
            adapter.data = ArrayList()
            DatePickerDialog(
                requireContext(),
                R.style.DialogTheme,
                datePicker,
                myCalendar.get(Calendar.YEAR),
                myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }


        val sdf = SimpleDateFormat("dd/M/yyyy")
        val currentDate = sdf.format(Date())
        binding.date.setText(currentDate)

        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getDates(today: DeviceDate, adapter: DeviceAdapter) {

        adapter.data = ArrayList()
        dayDevices = ArrayList()
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    Log.println(Log.DEBUG, String(), "Success")
                    for (document in documents) {
                        Log.println(Log.DEBUG, String(), (activity as LoggedActivity).dateParser(document.data["time"].toString()).toString()+" vs "+today.toString())
                        if (today.equals((activity as LoggedActivity).dateParser(document.data["time"].toString()))) {
                            Log.println(Log.DEBUG, String(), "Igual")
                            dayDevices.add(
                                Device(
                                    document.data.get("name") as String,
                                    document.data.get("address") as String,
                                    document.data.get("friend")!! as Boolean,
                                    document.data.get("black")!! as Boolean,
                                    (activity as LoggedActivity).locationParser(document.data["position"].toString())
                                )
                            )
                        }
                        adapter.data = dayDevices
                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }

    }

    private fun updateLabel(myCalendar: Calendar, date: TextView) {
        val myFormat = "dd-MM-yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.UK)
        date.setText(sdf.format(myCalendar.time))

    }
}
