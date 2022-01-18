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
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.stalktracker.databinding.ActivityLoggedBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import models.Device
import java.time.LocalDateTime


class LoggedActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    lateinit var auth : FirebaseAuth
    lateinit var navigationView: NavigationView

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityLoggedBinding>(this, R.layout.activity_logged)
        val navController = this.findNavController(R.id.myNavHostFragmentLogged)
        drawerLayout=binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navigationView=findViewById(R.id.navView)

        var settings = firestoreSettings {
            isPersistenceEnabled = true
        }

        settings = FirebaseFirestoreSettings.Builder()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()

        FirebaseUtils().fireStoreDatabase.firestoreSettings = settings

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


    fun changeNav(){
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.navdrawer_menu_logged)
    }



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
