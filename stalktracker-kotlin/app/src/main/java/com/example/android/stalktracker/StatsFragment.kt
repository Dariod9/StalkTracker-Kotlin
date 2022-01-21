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
import models.Device
import models.DeviceAdapter

class StatsFragment : Fragment() {
    private val adapter = DeviceAdapter()
    private lateinit var auth: FirebaseAuth
    private var friends: ArrayList<Device> = ArrayList()


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

//        adapter.setOnItemClickListener(object : DeviceAdapter.onItemClickListener {
//            override fun onItemClick(position: Int) {
//                val item = adapter.data[position]
////                adapter.removeFriendAlert(context, act, item
//                getPositionsByDevice(item.address)
////                Log.println(Log.DEBUG, String(), item.positions[0].latitude.toString())
//                val bundle = bundleOf("name" to item.name, "address" to item.address, "friend" to item.friend.toString(),
//                    "stalker" to item.black.toString(), "positions" to item.positions)
//                view?.findNavController()?.navigate(R.id.action_friendsListFragment_to_profileFragment, bundle)
//
//            }
//
//        })


        return binding.root
    }

    private fun getPositionsByDevice(address: String) :  ArrayList<LatLng> {

        var positions : ArrayList<LatLng> = ArrayList()

        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("address", address)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        Log.println(Log.DEBUG, String(), "Encontrou o Device")
                        if (document.data.get("positions") != null) {
                            var tmp = (document.data.get("positions") as List<*>)
                            //                        tmp.filter { it -> (it as String).length>0 }.forEach(positions.add((activity as LoggedActivity).locationParser("$it") )}
                            for (pos in tmp) {
                                Log.println(Log.DEBUG, String(), "Pos:"+pos.toString())
                                positions.add((activity as LoggedActivity).locationParser(pos.toString()))
                            }
                        }
                    }
                }
                .addOnFailureListener { exception ->
                }
        }

        return positions

    }


}
