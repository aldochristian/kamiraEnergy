package info.twentysixproject.kamiraenergy

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionForHome,
    ProfileFragment.OnFragmentInteractionForProfile{

    private var PRIVATE_MODE = 0
    internal var db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    var disableCallGrab: Boolean = true // Prevent Double activity called

    override fun onStart() {
        super.onStart()
        disableCallGrab = false
    }

    override fun onResume() {
        super.onResume()
        disableCallGrab = true
    }

    override fun onOptionsItemSelected(menuItem : MenuItem?) : Boolean {
        if (menuItem?.getItemId() == android.R.id.home) {
            onBackPressedDispatcher.onBackPressed()
        }
        return super.onOptionsItemSelected(menuItem)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // FCM initiate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.notification_channel_id)
            val channelName = getString(R.string.promotion_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_LOW)
            )
        }

        //val userID: String = intent.getStringExtra("uid")
        if(firstTimeLogin()){
            currentToken(user!!.uid) // FCM Token stored and check

            FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.promotion_notification_channel_name))
                .addOnCompleteListener { task ->
                    var msg = getString(R.string.msg_subscribed)
                    if (!task.isSuccessful) {
                        msg = getString(R.string.msg_subscribe_failed)
                    }
                    Log.d(TAG, msg)
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                }
            // [END subscribe_topics]
        }

        // Navigation controller
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController

        setupBottomNavMenu(navController) //Bottom Module
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
        //End of navigation controller

        // FCM : Handle possible data accompanying notification message.
        intent.extras?.let {
            for (key in it.keySet()) {
                val value = intent.extras?.get(key)
                Log.d(TAG, "Key: $key Value: $value")
            }
        }// FCM End

        Log.d(TAG, "OnStart UID "+user?.uid)
    }

    //======Navigation bottom menu=========//
    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNav?.setupWithNavController(navController)

    } // End of navigation bottom menu

    //======FCM Token======//
    private fun currentToken(userUid: String){

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w(TAG, "getInstanceId failed", task.exception)
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token
                Log.d(TAG, token)

                // Stored token
                db.collection("users").document(userUid)
                    .update("fcmToken", token)
                    .addOnSuccessListener { Log.d(TAG, "Success stored FCM token") }
                    .addOnFailureListener { Log.d(TAG, "Fail to store FCM token") }
            })
    }

    fun firstTimeLogin(): Boolean{
        val mSettings: SharedPreferences = getSharedPreferences("Settings",PRIVATE_MODE);
        val firstTimeFlag = mSettings.getBoolean("firstTimeFlag", true)

        if(firstTimeFlag){
            return true
        }
        val editor: SharedPreferences.Editor  = mSettings.edit()
        editor.putBoolean("firstTimeFlag", false)
        editor.apply()

        return false
    }

    companion object {
        private const val TAG = "MainActivity"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
    }

    override fun openGmaps() {
        if(disableCallGrab) {
            disableCallGrab = false
            val intent = Intent(this, MapsActivityJava::class.java)
            startActivity(intent)
        }
    }

    override fun logout() {
        auth.signOut()
        finish()
    }

}
