package com.example.android.stalktracker

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class DeviceAdapter: RecyclerView.Adapter<Util.ButtonItemViewHolder>() {

    var data =  listOf<Device>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: Util.ButtonItemViewHolder, position: Int) {
        val item=data[position]
        if(item.name.length>1)
            holder.button.text ="  "+item.name+"\n  ("+item.address+")"
        else
            holder.button.text="  "+item.address
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Util.ButtonItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.fragment_device, parent, false) as Button

        return Util.ButtonItemViewHolder(view)
    }

    fun friendAlert(context: Context?, act: LoggedActivity, device: Device){

        val builder = AlertDialog.Builder(context)
        Log.println(Log.DEBUG, String(), "Entrou no Alerta")

        with(builder)
        {
            setTitle("Androidly Alert")
            builder.setMessage("Add as:")
                .setPositiveButton("Friend",
                    DialogInterface.OnClickListener { dialog, id ->
                        act.addFriend(device)
                        // FIRE ZE MISSILES!
                    })
                .setNeutralButton("Stalker",
                    DialogInterface.OnClickListener { dialog, id ->
                        act.addBlack(device)
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton("No",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User cancelled the dialog
                    })
            show()
        }


    }


}