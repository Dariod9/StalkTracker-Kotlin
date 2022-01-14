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

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.example.android.stalktracker.databinding.ActivityLoggedBinding
import com.example.android.stalktracker.databinding.ActivityMainBinding
import com.firebase.ui.auth.AuthUI
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoggedActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private var friend_devices: ArrayList<Device> = ArrayList()
    private var black_devices: ArrayList<Device> = ArrayList()
    lateinit var auth : FirebaseAuth
    lateinit var navigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityLoggedBinding>(this, R.layout.activity_logged)
        val navController = this.findNavController(R.id.myNavHostFragmentLogged)
        drawerLayout=binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navigationView=findViewById(R.id.navView)
        black_devices.add(Device("Stalker1", "Endereço"))
        friend_devices.add(Device("Friend1", "Endereço"))
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragmentLogged)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    fun getFriends(): ArrayList<Device> {
        return friend_devices
    }

    fun addFriend(friend: Device?){
        if (friend != null) {
            friend_devices.add(friend)
        }
    }

    fun getBlack(): ArrayList<Device> {
        return friend_devices
    }

    fun addBlack(friend: Device?){
        if (friend != null) {
            black_devices.add(friend)
        }
    }


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
