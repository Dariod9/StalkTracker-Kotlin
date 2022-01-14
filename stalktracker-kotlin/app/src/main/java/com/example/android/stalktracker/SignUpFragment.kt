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

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentGameWonBinding
import com.example.android.stalktracker.databinding.FragmentSignupBinding
import com.example.android.stalktracker.databinding.FragmentTitleBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth

class SignUpFragment : Fragment() {
    private lateinit var  auth: FirebaseAuth

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        auth=FirebaseAuth.getInstance()

        val binding: FragmentSignupBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_signup, container, false)

        binding.signUp.setOnClickListener {
            Log.println(Log.DEBUG, String(), "CARREGOU")
            register(binding.editTextTextEmailAddress.text.toString(), binding.editTextTextPassword.text.toString())
        }

        // Inflate the layout for this fragment


        return binding.root
    }

    fun register(email: String, pwd: String){

        auth.createUserWithEmailAndPassword(email,pwd).addOnCompleteListener { task ->
            if(task.isSuccessful){
//                val mySnackbar = view?.let { Snackbar.make(it,"Registration Succesfull!", 3) }
                Toast.makeText(activity?.applicationContext,"Registration Succesfull!",Toast.LENGTH_SHORT).show()
//                Log.println(Log.DEBUG, String(), "DEU")

            }
        }.addOnFailureListener { exception ->
//            val mySnackbar = view?.let { Snackbar.make(it, "ERROR!", 4) }
            Toast.makeText(activity?.applicationContext,"ERROR",Toast.LENGTH_SHORT).show()

//            Log.println(Log.DEBUG, String(), "NÃO DEU")

        }
    }
}
