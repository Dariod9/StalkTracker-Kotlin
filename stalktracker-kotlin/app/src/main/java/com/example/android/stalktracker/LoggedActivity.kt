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

import android.Manifest
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.stalktracker.databinding.ActivityLoggedBinding
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import models.Device
import models.DeviceDate
import org.json.JSONArray
import org.json.JSONObject
import java.time.LocalDateTime
import java.util.*
import kotlin.collections.ArrayList


class LoggedActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    lateinit var auth : FirebaseAuth
    lateinit var navigationView: NavigationView
    lateinit var sp : SharedPreferences

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sp  = getSharedPreferences("login", MODE_PRIVATE)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityLoggedBinding>(this, R.layout.activity_logged)
        val navController = this.findNavController(R.id.myNavHostFragmentLogged)
        drawerLayout=binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navigationView=findViewById(R.id.navView)

        navigationView.menu.findItem(R.id.logout).setOnMenuItemClickListener { menuItem ->
            sp.edit().putBoolean("logged",false).apply();
            finish()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            true
        }

//        var settings = firestoreSettings {
//            isPersistenceEnabled = true
//        }
//
//        settings = FirebaseFirestoreSettings.Builder()
//            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
//            .build()
//
//        FirebaseUtils().fireStoreDatabase.firestoreSettings = settings

        val locationPermissionCheck =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        
        if (locationPermissionCheck != PackageManager.PERMISSION_GRANTED) {
            Log.println(Log.DEBUG, String(), "Location denied")
//            Toast.makeText(mContext, "Location permission denied", Toast.LENGTH_SHORT).show()
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),0
            )
        } else {
            Log.println(Log.DEBUG, String(), "Location granted")
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragmentLogged)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

//
//    fun getNum(){
////        Log.println(Log.DEBUG, String(),deviceViewModel.numDevices.toString())
//        val ref=this
//        lifecycleScope.launch(Dispatchers.IO) {
//            ref.runOnUiThread {
//                Log.println(Log.DEBUG, String(), deviceViewModel.numDevices.toString())
//            }
//        }
//    }

    fun locationParser(loc: String) : LatLng {
//        Log.println(Log.DEBUG, String(), "A String: $loc")
//        var fSplit=loc.split(",")
//        Log.println(Log.DEBUG, String(), fSplit.get(0))
//        var lat=fSplit.get(0).split("=").get(1)
//        Log.println(Log.DEBUG, String(), lat)
//
//        var long=fSplit[1].split("=")[1]
        val jsonObj = JSONObject(loc)
        val map = jsonObj.toMap()

        return LatLng(map["latitude"].toString().toDouble(),map["longitude"].toString().toDouble())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun dateParser(date : String) : DeviceDate{
        val jsonObj = JSONObject(date)
        val map = jsonObj.toMap()
//       Log.println(Log.DEBUG, String(), map["dayOfWeek"].toString())
        val dev=DeviceDate(map["dayOfMonth"].toString().toInt(), map["monthValue"].toString().toInt(), map["month"].toString(), map["year"].toString().toInt() )
//        Log.println(Log.DEBUG, String(), dev.day.toString()+" "+dev.month+" "+dev.year.toString())

        return dev
    }


//    {dayOfWeek=THURSDAY, hour=12, month=JANUARY, year=2022, dayOfMonth=20, dayOfYear=20, monthValue=1, nano=245000000, chronology={calendarType=iso8601, id=ISO}

//    fun checkLogged() {
//        auth = Firebase.auth
//        if (auth.currentUser == null) {
//            // Not signed in, launch the Sign In activity
//            startActivity(Intent(this, SignInActivity::class.java))
//            finish()
//            return
//        }
//    }

//    fun signOut() {
//        AuthUI.getInstance().signOut(this)
//        startActivity(Intent(this, SignInActivity::class.java))
//        finish()
//    }

}

private fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith {
    when (val value = this[it])
    {
        is JSONArray ->
        {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }
        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else            -> value
    }
}
