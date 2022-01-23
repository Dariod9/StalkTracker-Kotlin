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

import android.app.AlertDialog
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentContactBinding
import com.example.android.stalktracker.databinding.FragmentProfileBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.WriterException

import android.widget.Toast

import com.firebase.ui.auth.AuthUI.getApplicationContext

import android.content.DialogInterface

import android.graphics.drawable.BitmapDrawable
import android.opengl.Visibility
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import com.firebase.ui.auth.AuthUI

import com.google.zxing.common.BitMatrix

import com.google.zxing.MultiFormatWriter
import com.journeyapps.barcodescanner.BarcodeEncoder
import android.content.Intent
import android.net.Uri


class ContactFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val binding: FragmentContactBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_contact, container, false)

        var url=""
        binding.imageView.setOnClickListener{
            Log.println(Log.DEBUG, String(),"Carregou")

            binding.myImageView.bringToFront();
            url="https://www.instagram.com/dariod99/"
            val encoder = BarcodeEncoder()
            val bitmap = encoder.encodeBitmap("https://www.instagram.com/dariod99/", BarcodeFormat.QR_CODE, 400, 400)
            binding.myImageView.setImageBitmap(bitmap)
            binding.myImageView.visibility=VISIBLE

        }

        binding.imageView3.setOnClickListener{
            Log.println(Log.DEBUG, String(),"Carregou")
            url="https://www.instagram.com/pedr0aalmeida/"

            binding.myImageView.bringToFront();
            val encoder = BarcodeEncoder()
            val bitmap = encoder.encodeBitmap("https://www.instagram.com/pedr0aalmeida/", BarcodeFormat.QR_CODE, 400, 400)
            binding.myImageView.setImageBitmap(bitmap)
            binding.myImageView.visibility=VISIBLE

        }

        binding.myImageView.setOnClickListener{
            binding.myImageView.visibility= GONE
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(browserIntent)

        }

        return binding.root
    }


    fun getQrCodeBitmap(): Bitmap {
        val size = 512 //pixels
        val qrCodeContent = "https://www.instagram.com/dariod99/"//"WIFI:S:$ssid;T:WPA;P:$password;;"
        val hints = hashMapOf<EncodeHintType, Int>().also { it[EncodeHintType.MARGIN] = 1 } // Make the QR code buffer border narrower
        val bits = QRCodeWriter().encode(qrCodeContent, BarcodeFormat.QR_CODE, size, size)
        return Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565).also {
            for (x in 0 until size) {
                for (y in 0 until size) {
                    it.setPixel(x, y, if (bits[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        }
    }
}
