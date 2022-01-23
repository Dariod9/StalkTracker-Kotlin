package models

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.text.Html
import android.util.Log
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.android.stalktracker.FirebaseUtils
import com.example.android.stalktracker.LoggedActivity
import com.example.android.stalktracker.R
import com.example.android.stalktracker.Util
import com.google.firebase.auth.FirebaseAuth

class DeviceAdapter: RecyclerView.Adapter<ButtonItemViewHolder>() {

    private lateinit var mListener : onItemClickListener
    private var auth : FirebaseAuth = FirebaseAuth.getInstance()

    interface onItemClickListener{

        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){

        mListener = listener
    }

    var data =  listOf<Device>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun getItemCount() = data.size

    override fun onBindViewHolder(holder: ButtonItemViewHolder, position: Int) {
        val item=data[position]
        if(item.name.length>1)
            holder.button.text ="  "+item.name+"\n  ("+item.address+")"
        else
            holder.button.text="  "+item.address
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ButtonItemViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater
            .inflate(R.layout.fragment_device, parent, false) as Button

        return ButtonItemViewHolder(view, mListener)
    }

    fun friendAlert(context: Context?, act: LoggedActivity, device: Device){

        val builder = AlertDialog.Builder(context)
        Log.println(Log.DEBUG, String(), "Entrou no Alerta")

        with(builder)
        {
            setTitle("Add as:")
                .setPositiveButton(Html.fromHtml("<font color='#35A571'>Friend</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        addFriend(device)
                        // FIRE ZE MISSILES!
                    })
                .setNeutralButton(Html.fromHtml("<font color='#323230'>Cancel</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton(Html.fromHtml("<font color='#F44336'>Stalker</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        addBlack(device)

                        // User cancelled the dialog
                    })
            show()
        }


    }

    fun stalkerAlert(context: Context?, act: LoggedActivity){
        val builder = AlertDialog.Builder(ContextThemeWrapper(context, R.style.AlertDialogTheme))
        Log.println(Log.DEBUG, String(), "Entrou no Alerta")

        with(builder)
        {
            setTitle("STALKER DETECTED:")
                .setPositiveButton(Html.fromHtml("<font color='#323230'>OK</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        // FIRE ZE MISSILES!
                    })
            show()
        }
    }

    fun removeFriendAlert(context: Context?, act: LoggedActivity, device: Device){

        val builder = AlertDialog.Builder(context)
        Log.println(Log.DEBUG, String(), "Entrou no Alerta")

        with(builder)
        {
            setTitle("Remove as Friend?")
                .setPositiveButton(Html.fromHtml("<font color='#323230'>Yes</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        removeFriend(device)
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton(Html.fromHtml("<font color='#323230'>No</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
//                        addBlack(device)
                        // FIRE ZE MISSILES!
                    })

            show()
        }
    }

    fun removeStalkerAlert(context: Context?, act: LoggedActivity, device: Device) : Boolean{
        var result=false
        val builder = AlertDialog.Builder(context)
        Log.println(Log.DEBUG, String(), "Entrou no Alerta")

        with(builder)
        {
            setTitle("Remove as Stalker?")
                .setPositiveButton(Html.fromHtml("<font color='#323230'>Yes</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
                        removeStalker(device)
                        result=true
                        // FIRE ZE MISSILES!
                    })
                .setNegativeButton(Html.fromHtml("<font color='#323230'>No</font>"),
                    DialogInterface.OnClickListener { dialog, id ->
//                        addBlack(device)
                        // FIRE ZE MISSILES!
                    })

            show()
        }

        return result
    }


    fun addFriend(device : Device){
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                .collection("users")
                .document(device.address)
                .update("friend", true)
                .addOnSuccessListener {
                    Log.println(Log.DEBUG, String(), "Success")
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "Error!")
                }
        }

    }

    fun addBlack(device: Device){
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                .collection("users")
                .document(device.address)
                .update("black", true)
                .addOnSuccessListener {
                    Log.println(Log.DEBUG, String(), "Success")
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "Error!")
                }
        }

    }

    private fun removeFriend(device: Device) {
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                .collection("users")
                .document(device.address)
                .update("friend", false)
                .addOnSuccessListener {
                    Log.println(Log.DEBUG, String(), "Success")
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "Error!")
                }
        }

    }

    private fun removeStalker(device: Device) {
        auth.currentUser?.email?.let {
            FirebaseUtils().fireStoreDatabase.collection("Users").document(it)
                .collection("users")
                .document(device.address)
                .update("black", false)
                .addOnSuccessListener {
                    Log.println(Log.DEBUG, String(), "Success")
                }
                .addOnFailureListener { exception ->
                    Log.println(Log.DEBUG, String(), "Error!")
                }
        }

    }


}

class ButtonItemViewHolder(val button: Button, listener: DeviceAdapter.onItemClickListener): RecyclerView.ViewHolder(button){

    init {

        button.setOnClickListener {

            listener.onItemClick(adapterPosition)
        }
    }

}