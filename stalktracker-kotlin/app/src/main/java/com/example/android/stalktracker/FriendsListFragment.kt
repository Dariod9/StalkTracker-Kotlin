package com.example.android.stalktracker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import java.text.SimpleDateFormat
import java.util.*

class FriendsListFragment : Fragment() {
    private val adapter = DeviceAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentFriendslistBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_friendslist, container, false)

        binding.recyclerView.adapter=adapter
        val act= activity as LoggedActivity
        adapter.data=act.getFriends()

        return binding.root
    }


}
