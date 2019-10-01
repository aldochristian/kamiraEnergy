package info.twentysixproject.kamiraenergy.utils

import android.util.Log
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

object Utils {

    fun convertTimeFromFirebase(input: Timestamp): String{
        val timestamp = input
        val milliseconds = timestamp.seconds * 1000 + timestamp.nanoseconds / 1000000
        val sdf = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH)
        val netDate = Date(milliseconds)
        val date = sdf.format(netDate).toString()
        Log.d("Utils", date)
        return date
    }

    fun convertStringtoDate(input: String): LocalDate{
        val formatter = DateTimeFormatter.ofPattern("MM dd, yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(input, formatter)
        return date
    }
}