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
import com.google.android.gms.maps.model.LatLng
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentBlacklistBinding
import com.google.firebase.auth.FirebaseAuth
import models.Device
import models.DeviceAdapter
import models.FirebaseUtils

class BlackListFragment : Fragment() {
    private val adapter = DeviceAdapter()
    private lateinit var auth : FirebaseAuth
    private var stalkers: ArrayList<Device> = ArrayList()


    @RequiresApi(Build.VERSION_CODES.O)
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
                getPositionsByDevice(item.address)
                val bundle = bundleOf("name" to item.name, "address" to item.address, "friend" to item.friend.toString(),
                    "stalker" to item.black.toString(), "positions" to item.positions)
                view?.findNavController()?.navigate(R.id.action_blackListFragment_to_profileFragment, bundle)
            }

        })

        binding.button3.setOnClickListener{
            adapter.addBlack(Device("",binding.button3.text.toString(),false, false))
        }

        stalkers= ArrayList()
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("black", true)
                .get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {


                        stalkers.add(Device(document.data.get("name") as String,
                            document.data.get("address") as String,
                            document.data.get("friend") as Boolean,
                            document.data.get("black") as Boolean))

                        adapter.data=stalkers
                    }

                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }


        return binding.root
    }

    fun getPositionsByDevice(address: String) :  ArrayList<LatLng> {

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
