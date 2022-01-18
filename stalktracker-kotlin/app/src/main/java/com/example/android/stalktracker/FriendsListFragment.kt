package com.example.android.stalktracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import com.google.firebase.auth.FirebaseAuth
import models.Device
import models.DeviceAdapter

class FriendsListFragment : Fragment() {
    private val adapter = DeviceAdapter()
    private lateinit var auth: FirebaseAuth
    private var friends: ArrayList<Device> = ArrayList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val binding: FragmentFriendslistBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_friendslist, container, false
        )

        auth = FirebaseAuth.getInstance()

        val act = activity as LoggedActivity

        binding.recyclerView.adapter = adapter
        adapter.setOnItemClickListener(object : DeviceAdapter.onItemClickListener {
            override fun onItemClick(position: Int) {
                val item = adapter.data[position]
//                adapter.removeFriendAlert(context, act, item
                val bundle = bundleOf("name" to item.name, "address" to item.address, "friend" to item.friend.toString(), "stalker" to item.black.toString())
                view?.findNavController()?.navigate(R.id.action_friendsListFragment_to_profileFragment, bundle)

            }

        })

        friends= ArrayList()
//        auth.currentUser?.email?.let
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users")
                .document(it)
                .collection("users")
                .whereEqualTo("friend", true)
                .get()
                .addOnSuccessListener { documents ->
                    Log.println(Log.DEBUG, String(), "Entrou no success")
                    for (document in documents) {
                        friends.add(Device(document.data.get("name") as String,
                                            document.data.get("address") as String,
                                            document.data.get("friend") as Boolean,
                                            document.data.get("black") as Boolean))
                        adapter.data=friends
                    }
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "ERRO")
                }
        }

        Log.println(Log.DEBUG, String(), "Tamanho "+adapter.data.size.toString() )


        return binding.root
    }


}
