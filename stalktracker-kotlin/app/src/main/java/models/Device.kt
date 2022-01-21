package models

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.maps.model.LatLng
import java.lang.Math.abs
import java.time.LocalDateTime
import java.util.*

class Device {
    var name: String = ""
    var address: String = ""
//    val location: Location
@RequiresApi(Build.VERSION_CODES.O)
var time: LocalDateTime= LocalDateTime.now()
    var friend: Boolean = false
    var black: Boolean = false
    lateinit var positions: ArrayList<LatLng>

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(name: String, address: String, friend : Boolean, black : Boolean, positions : ArrayList<LatLng>) {
        this.name = name
        this.address = address
        this.friend=friend
        this.black=black
        this.positions=positions
        Log.println(Log.DEBUG, String(), time.toString())
    }

    constructor(name: String, address: String, friend : Boolean, black : Boolean) {
        this.name = name
        this.address = address
        this.friend=friend
        this.black=black
        this.positions=ArrayList<LatLng>()
    }

    override fun equals(other: Any?): Boolean {
        return this.name.equals((other as Device).name)
    }

    fun isSameLocation(pos1 : LatLng, pos2 : LatLng) : Boolean{

        return abs(abs(pos1.latitude)-abs(pos2.latitude))<1 && (abs(abs(pos1.longitude)-abs(pos2.longitude))<1)

    }

    override fun toString() : String{
        return this.name+"-"+this.address+"-"+this.friend+"-"+this.black+"-"+this.positions
    }

}

//    constructor(name: String, address: String, location : Location?) {
//        this.name = name
//        this.address = address
//        this.loc=location
//    }
//
//}