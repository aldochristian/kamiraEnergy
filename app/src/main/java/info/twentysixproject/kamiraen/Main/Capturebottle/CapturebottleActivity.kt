package info.twentysixproject.kamiraen.Main.Capturebottle

import android.content.res.Resources
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import info.twentysixproject.kamiraen.R

class CapturebottleActivity : AppCompatActivity() {

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
            try {
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
        if (menuItem?.itemId == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }
}
