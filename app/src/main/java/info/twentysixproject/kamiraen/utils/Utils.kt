package info.twentysixproject.kamiraen.utils

import android.app.Dialog
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import info.twentysixproject.kamiraen.R
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

     fun openCustomeLink(context: Context, url: String) {

         val customTabHelper: CustomTabHelper = CustomTabHelper()
        val builder = CustomTabsIntent.Builder()

        // modify toolbar color
        builder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary))
        // add share button to overflow menu
        builder.addDefaultShareMenuItem()

        val anotherCustomTab = CustomTabsIntent.Builder().build()
        val requestCode = 100
        val intent = anotherCustomTab.intent
        intent.data = Uri.parse(url)

        val pendingIntent = PendingIntent.getActivity(context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // add menu item to oveflow
        builder.addMenuItem("Sample item", pendingIntent)
        builder.setShowTitle(true)

        // animation for enter and exit of tab
        builder.setStartAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)
        builder.setExitAnimations(context, android.R.anim.fade_in, android.R.anim.fade_out)

        val customTabsIntent = builder.build()

        // check is chrom available
        val packageName = customTabHelper.getPackageNameToUse(context, url)

        if (packageName == null) {
            // if chrome not available open in web view
            //val intentOpenUri = Intent(this, WebViewActivity::class.java)
            //intentOpenUri.putExtra(WebViewActivity.EXTRA_URL, Uri.parse(PRIVACY_POLICY).toString())
            //startActivity(intentOpenUri)
        } else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(context, Uri.parse(url))
        }
    }

}