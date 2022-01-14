package com.example.android.stalktracker

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.example.android.stalktracker.databinding.FragmentBlacklistBinding

class BlackListFragment : Fragment() {
    private val adapter = DeviceAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        val binding: FragmentBlacklistBinding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_blacklist, container, false)

        binding.recyclerView.adapter=adapter
        val act= activity as LoggedActivity
        adapter.data=act.getBlack()
        Log.println(Log.DEBUG, String(), act.getBlack().size.toString())

        return binding.root
    }
}
