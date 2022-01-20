package com.example.android.stalktracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import com.google.android.gms.maps.model.LatLng
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentBlacklistBinding
import com.google.firebase.auth.FirebaseAuth
import models.Device
import models.DeviceAdapter

class BlackListFragment : Fragment() {
    private val adapter = DeviceAdapter()
    private lateinit var auth : FirebaseAuth
    private var stalkers: ArrayList<Device> = ArrayList()


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentBlacklistBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_blacklist, container, false)

        val act = activity as LoggedActivity
        auth = FirebaseAuth.getInstance()


        binding.recyclerView.adapter=adapter
        adapter.setOnItemClickListener(object  : DeviceAdapter.onItemClickListener{
            override fun onItemClick(position: Int) {
                val item=adapter.data[position]
                val bundle = bundleOf("name" to item.name, "address" to item.address, "friend" to item.friend.toString(), "stalker" to item.black.toString(),  "latitude" to item.position.latitude, "longitude" to item.position.longitude)
                view?.findNavController()?.navigate(R.id.action_blackListFragment_to_profileFragment, bundle)
            }

        })

        stalkers= ArrayList()
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("black", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
//                        var pos = LatLng((document.data.get("location") as Map<*, *>).get("latitude") as Double,(document.data.get("location") as Map<*, *>).get("longitude") as Double)
//                        Log.println(Log.DEBUG, String(), (document.data["location"]).toString())
                        var pos=LatLng(0.0,0.0)
                        if((document.data.get("location")!=null)){
                            pos=(activity as LoggedActivity).locationParser(document.data.get("location").toString())
                        }

                        stalkers.add(Device(document.data.get("name") as String,
                            document.data.get("address") as String,
                            document.data.get("friend") as Boolean,
                            document.data.get("black") as Boolean,
                            pos))

                        adapter.data=stalkers
                    }

                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }


        return binding.root
    }
}
