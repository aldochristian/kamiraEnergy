package info.twentysixproject.kamiraenergy.utils

import android.app.Dialog
import android.content.Context
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import com.google.firebase.Timestamp
import info.twentysixproject.kamiraenergy.R
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
        return date
    }

    fun convertStringtoDate(input: String): LocalDate{
        val formatter = DateTimeFormatter.ofPattern("MM dd, yyyy", Locale.ENGLISH)
        val date = LocalDate.parse(input, formatter)
        return date
    }

    fun warningDialog(context: Context, title: String, content: String){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.findViewById<TextView>(R.id.title).text = title
        dialog.findViewById<TextView>(R.id.content).text = content

        dialog.findViewById<Button>(R.id.bt_close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.attributes = lp
    }

    fun confirmDialog(context: Context, title: String, content: String){
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.setCancelable(true)

        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window?.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT

        dialog.findViewById<TextView>(R.id.title).text = title
        dialog.findViewById<TextView>(R.id.content).text = content

        dialog.findViewById<Button>(R.id.bt_close).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.attributes = lp
    }

}