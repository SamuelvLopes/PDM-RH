package com.incompanyapp.db.fb

import android.os.Build
import androidx.annotation.RequiresApi
import com.incompanyapp.model.Clock
import java.time.LocalDate
import java.time.LocalTime

class FBClock {
    var date : LocalDate? = null
    var clockIn : LocalTime? = null
    var clockOut : LocalTime? = null
    @RequiresApi(Build.VERSION_CODES.O)
    fun toClock(): Clock {
        val clockIn = clockIn ?: LocalTime.now()
        return Clock(date!!, clockIn!!, clockOut)
    }
}
fun Clock.toFBClock() : FBClock {
    val fbClock = FBClock()
    fbClock.date = this.date
    fbClock.clockIn = this.clockIn
    fbClock.clockOut = this.clockOut
    return fbClock
}
