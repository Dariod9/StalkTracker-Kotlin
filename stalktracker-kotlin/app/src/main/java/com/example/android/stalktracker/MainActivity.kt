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

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.android.stalktracker.databinding.ActivityMainBinding
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    lateinit var auth : FirebaseAuth
    private lateinit var navigationView: NavigationView
    private var isNotificationOn : Boolean = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("UNUSED_VARIABLE")
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
        val navController = this.findNavController(R.id.myNavHostFragment)
        drawerLayout=binding.drawerLayout
        NavigationUI.setupActionBarWithNavController(this, navController, drawerLayout)
        NavigationUI.setupWithNavController(binding.navView, navController)
        navigationView=findViewById(R.id.navView)

        createNotificationChannel()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = this.findNavController(R.id.myNavHostFragment)
        return NavigationUI.navigateUp(navController, drawerLayout)
    }

    fun setActionBarTitle(title: String?) {
         supportActionBar?.title =title
    }

    fun changeNav(){
        navigationView.menu.clear()
        navigationView.inflateMenu(R.menu.navdrawer_menu_logged)
//        val appBarConfiguration = AppBarConfiguration(setOf(R.id.firstFragment, R.id.secondFragment))
//        NavigationUI.navigateUp(navController, drawerLayout)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.option_menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        super.onPrepareOptionsMenu(menu)
        val item = menu.findItem(R.id.notifications)
        if (isNotificationOn){
            item.setIcon(R.drawable.ic_baseline_notifications_24)
        }
        else {
            item.setIcon(R.drawable.ic_baseline_notifications_off_24)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.notifications){
            if (isNotificationOn){
                cancelRecurringNotification()
                Toast.makeText(applicationContext,"Notifications off",Toast.LENGTH_SHORT).show()
            }
            else{
                scheduleRecurringNotification()
                Toast.makeText(applicationContext,"Notifications on",Toast.LENGTH_SHORT).show()
            }
            isNotificationOn = !isNotificationOn
            invalidateOptionsMenu()
            return true
        }
        return NavigationUI.onNavDestinationSelected(item, findNavController(R.id.myNavHostFragment))
                || super.onOptionsItemSelected(item)
    }

    private fun getPendingIntent(): PendingIntent {
        val intent = Intent(applicationContext, Notification::class.java)

        return PendingIntent.getBroadcast(
            applicationContext,
            notificationID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    private fun getAlarmManager(): AlarmManager {
        return getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }

    private fun scheduleRecurringNotification(){
        val pendingIntent = getPendingIntent()

        val updateTime: Calendar = Calendar.getInstance()
        updateTime.timeZone = TimeZone.getTimeZone("GMT")
        updateTime.set(Calendar.HOUR_OF_DAY, 12)
        updateTime.set(Calendar.MINUTE, 0)

        val alarmManager = getAlarmManager()
        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            updateTime.timeInMillis,
            AlarmManager.INTERVAL_FIFTEEN_MINUTES,
            pendingIntent
        )
    }

    private fun cancelRecurringNotification(){
        val pendingIntent = getPendingIntent()

        val alarmManager = getAlarmManager()
        alarmManager.cancel(pendingIntent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(){
        val name = "Notification Channel"
        val description = "A description of the channel"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelID, name, importance)
        channel.description = description

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
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
