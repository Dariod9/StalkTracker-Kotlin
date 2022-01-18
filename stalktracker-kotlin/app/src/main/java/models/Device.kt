package models

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import java.time.LocalDateTime
import java.util.*

class Device {
    var name: String = ""
    var address: String = ""
//    val location: Location
@RequiresApi(Build.VERSION_CODES.O)
var time: LocalDateTime = LocalDateTime.now()
    var friend: Boolean = false
    var black: Boolean = false

    constructor(name: String, address: String, friend : Boolean, black : Boolean) {
        this.name = name
        this.address = address
        this.friend=friend
        this.black=black
    }

    override fun equals(other: Any?): Boolean {
        return this.name.equals((other as Device).name)
    }


}

//    constructor(name: String, address: String, location : Location?) {
//        this.name = name
//        this.address = address
//        this.loc=location
//    }
//
//}