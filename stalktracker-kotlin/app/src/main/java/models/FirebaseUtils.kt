package models

import android.bluetooth.BluetoothClass
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings
import models.Device

class FirebaseUtils {

    lateinit var auth : FirebaseAuth
    var initialized = false
    val fireStoreDatabase = giveDB()


    private fun giveDB(): FirebaseFirestore {

        auth = FirebaseAuth.getInstance()
        var db : FirebaseFirestore

        if(!initialized) {
            val settings = FirebaseFirestoreSettings.Builder()
//            .isPersistenceEnabled()
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .setPersistenceEnabled(true)
                .build()
            db = FirebaseFirestore.getInstance()
            Log.println(Log.DEBUG, String(), db.firestoreSettings.isPersistenceEnabled.toString())
            db.firestoreSettings = settings

            this.initialized=true
        }
        else{
            db = FirebaseFirestore.getInstance()

        }

        return db
    }

}