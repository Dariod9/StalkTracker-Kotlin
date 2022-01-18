package com.example.android.stalktracker

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestoreSettings

class FirebaseUtils {
//    val bool = setSettings()
//
//    private fun setSettings(): Boolean {
//
//        fireStoreDatabase.firestoreSettings=FirebaseFirestoreSettings.Builder()
//            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
//            .build()
//        return true
//    }

    val fireStoreDatabase = giveDB()

    private fun giveDB(): FirebaseFirestore {

        val settings= FirebaseFirestoreSettings.Builder()
//            .isPersistenceEnabled()
            .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
            .build()
        val db=FirebaseFirestore.getInstance()
        Log.println(Log.DEBUG, String(), db.firestoreSettings.isPersistenceEnabled.toString())
        db.firestoreSettings=settings

        return db
    }
}