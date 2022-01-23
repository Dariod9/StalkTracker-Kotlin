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
    var nPos : Int = 0

    @RequiresApi(Build.VERSION_CODES.O)
    constructor(name: String, address: String, friend : Boolean, black : Boolean, positions : ArrayList<LatLng>, nPos : Int) {
        this.name = name
        this.address = address
        this.friend=friend
        this.black=black
        this.positions=positions
        this.nPos=nPos
        Log.println(Log.DEBUG, String(), time.toString())
    }

    constructor(name: String, address: String, friend : Boolean, black : Boolean) {
        this.name = name
        this.address = address
        this.friend=friend
        this.black=black
        this.positions=ArrayList<LatLng>()
        this.nPos=positions.size
    }

    override fun equals(other: Any?): Boolean {
        return this.name.equals((other as Device).name)
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