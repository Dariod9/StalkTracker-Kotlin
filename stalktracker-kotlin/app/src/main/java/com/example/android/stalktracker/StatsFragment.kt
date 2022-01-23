package com.example.android.stalktracker

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import com.example.android.stalktracker.databinding.FragmentStatsBinding
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Query
import models.Device
import models.DeviceAdapter

class StatsFragment : Fragment() {
    private val adapter = DeviceAdapter()
    private lateinit var auth: FirebaseAuth
    private var friends: ArrayList<Device> = ArrayList()
    private lateinit var addressM: String


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentStatsBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_stats, container, false
        )

        auth = FirebaseAuth.getInstance()

        val act = activity as LoggedActivity

        var name = ""
        var address = ""
        var friend = false
        var black = false
        var encounters = 0
        var date = ""


//        var stalker= findStalker()
        var max = 0
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users").orderBy("npos", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        if (document == documents.first()) {
                            if (document.data.get("positions") != null) {
                                encounters = (document.data.get("positions") as List<*>).size
                                name = (document.data["name"].toString())
                                friend = (document.data["friend"].toString()).toBoolean()
                                black = (document.data["black"].toString()).toBoolean()
                                address = (document.data["address"].toString())
                                date = (document.data["time"].toString())
                                Log.println(Log.DEBUG, String(), date)
                                Log.println(
                                    Log.DEBUG,
                                    String(),
                                    "HERE" + ((activity as LoggedActivity).dateParser(date)).toString()
                                )
                                addressM = address
                                Log.println(Log.DEBUG, String(), "Endereco:" + address)

                                binding.nameStat.text = name
                                binding.addressStat.text = "(" + address + ")"
                                binding.locationsStat.text = encounters.toString() + " times"
                                binding.locationsStat.setOnClickListener {
                                    val bundle = bundleOf(
                                        "name" to name, "address" to address, "friend" to friend,
                                        "stalker" to black
                                    )
                                    view?.findNavController()?.navigate(
                                        R.id.action_statsFragment_to_profileFragment,
                                        bundle
                                    )
                                }

                                binding.dateStat.text=((activity as LoggedActivity).dateParser(date)).toString()
                                binding.dateStat.setOnClickListener {
                                    view?.findNavController()?.navigate(R.id.action_statsFragment_to_historyFragment)
                                }


                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }
        Thread.sleep(700)


//        auth.currentUser?.email?.let {
//            FirebaseUtils().fireStoreDatabase.collection("Users")
//                .document(it)
//                .collection("users")
//                .whereEqualTo("address", stalker)
//                .get()
//                .addOnSuccessListener { documents ->
//                    for (document in documents) {
//                        Log.println(Log.DEBUG, String(), "Encontrou o Device")
//                        if (document.data.get("positions") != null) {
//                            encounters = (document.data.get("positions") as List<*>).size
//                            name = (document.data["name"].toString())
//                            friend =(document.data["friend"].toString()).toBoolean()
//                            black =(document.data["black"].toString()).toBoolean()
//                            address = (document.data["address"].toString())
//                            date=(document.data["time"].toString())
//                            Log.println(Log.DEBUG, String(), date)
//                            Log.println(Log.DEBUG, String(), "HERE"+((activity as LoggedActivity).dateParser(date)).toString())
//
//
//                        }
//                    }
//                }
//                .addOnFailureListener { exception ->
//                }
//        }

//        Thread.sleep(5000)
//        Log.println(Log.DEBUG, String(), "Endereco:"+address)






        return binding.root
    }

    private fun findStalker() {

        var max = 0
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.println(Log.DEBUG, String(), "Encontrou o Device")
                        if (document.data.get("positions") != null) {

                            var tmp = (document.data.get("positions") as List<*>)
                            //                        tmp.filter { it -> (it as String).length>0 }.forEach(positions.add((activity as LoggedActivity).locationParser("$it") )}

                            if (tmp.size > max) {
                                max = tmp.size
//                                addressM=document.data["address"].toString()

                            }

                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }

    }


}
