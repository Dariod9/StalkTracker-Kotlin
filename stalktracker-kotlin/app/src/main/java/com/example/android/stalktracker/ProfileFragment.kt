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

import android.opengl.Visibility
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import com.example.android.stalktracker.databinding.FragmentProfileBinding
import com.example.android.stalktracker.databinding.FragmentProfileBindingImpl
import models.Device
import models.DeviceAdapter
import java.text.SimpleDateFormat
import java.util.*

class ProfileFragment : Fragment() {
    private val adapter = DeviceAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentProfileBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_profile, container, false)

        val device= Device(arguments?.getString("name").toString(),
                            arguments?.getString("address").toString(),
                            arguments?.getString("friend").toBoolean(),
                            arguments?.getString("stalker").toBoolean())

        binding.name.setText(device.name)
        binding.mac.setText(device.address)
        binding.friend.setText(device.friend.toString())
        binding.stalker.setText(device.black.toString())

        if(!device.friend) binding.rmFriend.visibility=View.INVISIBLE
        if(!device.black) binding.rmStalker.visibility=View.INVISIBLE

        binding.rmStalker.setOnClickListener {
            val act = activity as LoggedActivity
            adapter.removeStalkerAlert(context, act, device)
        }

        binding.rmFriend.setOnClickListener {
            val act = activity as LoggedActivity
            adapter.removeFriendAlert(context, act, device)
        }

        return binding.root
    }
}
