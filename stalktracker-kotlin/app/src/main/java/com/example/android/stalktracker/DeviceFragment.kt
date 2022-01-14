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

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentDeviceBinding
import com.example.android.stalktracker.databinding.FragmentFriendslistBinding
import java.util.*

class DeviceFragment : Fragment() {
    private val act= activity as LoggedActivity
    private val adapter = DeviceAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentDeviceBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_device, container, false)

        binding.dev.setOnClickListener{

            Log.println(Log.DEBUG, String(), "Button successful")
            val deviceString=binding.dev.text.trim().toString()
            var device=Device("a","b")
            if(deviceString.contains("\n")){
                val ar=deviceString.split("\n")
                device=Device(ar[1],ar[2].removePrefix("(").removePrefix(")"))
            }
            else{
                device=Device("None",deviceString)
            }
            adapter.friendAlert(context, act, device)
        }

        return binding.root
    }

}
