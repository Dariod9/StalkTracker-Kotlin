package models

import android.util.Log
import com.google.android.gms.maps.model.LatLng

class DeviceDate {
    var day: Int = 0
    var month: Int = 0
    var monthString: String = ""
    var year : Int = 0

    constructor(day: Int, month: Int, monthString: String, year : Int) {
        this.day= day
        this.month = month
        this.monthString = monthString
        this.year=year
    }

    fun equals(d2 : DeviceDate) : Boolean{
        return (this.day==d2.day && this.month == d2.month && this.year==d2.year)
    }

    override fun toString(): String {
        return this.day.toString()+" "+this.month+" "+this.year
    }
}
