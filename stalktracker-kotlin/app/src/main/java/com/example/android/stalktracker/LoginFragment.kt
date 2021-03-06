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
import androidx.navigation.findNavController
import com.example.android.stalktracker.MainActivity
import com.example.android.stalktracker.databinding.FragmentLoginBinding
import com.example.android.stalktracker.databinding.FragmentLoginBindingImpl
import com.example.android.stalktracker.databinding.FragmentSignupBinding
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import android.content.Intent
import android.widget.Toast


class LoginFragment : Fragment() {

    private lateinit var  auth: FirebaseAuth
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        // Inflate the layout for this fragment
        auth=FirebaseAuth.getInstance()
//        (activity as MainActivity).sp.edit().putBoolean("logged",false).apply();


        val binding: FragmentLoginBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_login, container, false)

        binding.buttonSignIn.setOnClickListener {
            login(binding.editTextTextEmailAddress.text.toString(), binding.editTextTextPassword.text.toString())
        }
        return binding.root
    }

    fun login(email: String, pwd: String){

        auth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Log.println(Log.DEBUG, String(), "DEU")
                val toast = Toast.makeText(context, "Logging in..", Toast.LENGTH_LONG)
                toast.show()
                (activity as MainActivity).sp.edit().putBoolean("logged",true).apply();
//                (activity as MainActivity).changeNav()
//                view?.findNavController()?.navigate(R.id.action_loginFragment_to_afterLoginFragment)
                val intent = Intent(activity, LoggedActivity::class.java)
                startActivity(intent)
                (activity as MainActivity).finish()
            }
        }.addOnFailureListener { exception ->
            Log.println(Log.DEBUG, String(), "N??O DEU")
            val toast = Toast.makeText(context, "Error: "+exception, Toast.LENGTH_LONG)
            toast.show()


        }
    }
}
