package com.chronomaster.sample

import android.app.Application
import com.chronomaster.library.ChronoMaster

class SampleApp : Application() {

    override fun onCreate() {
        super.onCreate()
        ChronoMaster.initialize(
            inputTimeZoneId = "UTC",
            outputTimeZoneId = "Asia/Kolkata"
        )
    }
}
