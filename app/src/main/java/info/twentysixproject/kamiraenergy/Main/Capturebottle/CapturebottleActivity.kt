package info.twentysixproject.kamiraenergy.Main.Capturebottle

import android.content.res.Resources
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import info.twentysixproject.kamiraenergy.R

import kotlinx.android.synthetic.main.activity_capturebottle.*
import java.io.*
import java.util.*

class CapturebottleActivity : AppCompatActivity() {
    val TAG:String = "CapturebottleActivity"

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: String? = FirebaseAuth.getInstance().uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_capturebottle)
        //setSupportActionBar(toolbar)

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.capturebottle_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val dest: String = try {
                resources.getResourceName(destination.id)
            } catch (e: Resources.NotFoundException) {
                Integer.toString(destination.id)
            }

            /**
             * This Toast to help you know the navigation, please remove.
             * Toast.makeText(this@MainActivity, "Navigated to $dest", Toast.LENGTH_SHORT).show()
             * Log.d(TAG, "Navigated to $dest")
             */
        }

    }

    override fun onOptionsItemSelected(menuItem : MenuItem?) : Boolean {
        if (menuItem?.getItemId() == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}
