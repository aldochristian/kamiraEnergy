package info.twentysixproject.kamiraen.utils

import android.util.Log
import com.crashlytics.android.Crashlytics

object Crash {
    fun logReportAndPrint(tag: String, msg: String) {
        Crashlytics.log(Log.DEBUG, tag, msg)
    }

    fun logReportOnly(msg: String) {
        Crashlytics.log(msg)
    }
}