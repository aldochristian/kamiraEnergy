package info.twentysixproject.kamiraenergy.Main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.functions.FirebaseFunctions
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import info.twentysixproject.kamiraenergy.Main.Capturebottle.CapturebottleActivity
import info.twentysixproject.kamiraenergy.Main.Fragments.HomeFragment
import info.twentysixproject.kamiraenergy.Main.Fragments.ProfileFragment
import info.twentysixproject.kamiraenergy.R
import info.twentysixproject.kamiraenergy.utils.CustomTabHelper

class MainActivity : AppCompatActivity(),
    HomeFragment.OnFragmentInteractionForHome,
    ProfileFragment.OnFragmentInteractionForProfile{

    private var PRIVATE_MODE = 0
    internal var db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: String? = FirebaseAuth.getInstance().uid

    private lateinit var functions: FirebaseFunctions

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
        if (menuItem?.itemId == android.R.id.home) {
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
        functions = FirebaseFunctions.getInstance()
        currentToken(user) // FCM Token stored and check

        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.promotion_notification_channel_name))
            .addOnCompleteListener { task ->
                //var msg = getString(R.string.msg_subscribed)
                if (!task.isSuccessful) {
                    //msg = getString(R.string.msg_subscribe_failed)
                }
                //Log.d(TAG, msg)
                //Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        // Navigation controller
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.my_nav_host_fragment) as NavHostFragment? ?: return
        val navController = host.navController

        setupBottomNavMenu(navController) //Bottom Module
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
        //End of navigation controller

        // FCM : Handle possible data accompanying notification message.
        intent.extras?.let {
            //for (key in it.keySet()) { }
        }// FCM End
    }

    //======Navigation bottom menu=========//
    private fun setupBottomNavMenu(navController: NavController) {
        val bottomNav = findViewById<BottomNavigationView>(R.id.nav_view_bottom)
        bottomNav?.setupWithNavController(navController)

    } // End of navigation bottom menu

    //======FCM Token======//
    private fun currentToken(userUid: String?){

        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    return@OnCompleteListener
                }
                // Get new Instance ID token
                val token = task.result?.token

                // Stored token
                val checkDoc = db.collection("users").document(userUid!!)

                checkDoc.update("lastLogin", FieldValue.serverTimestamp())
                    .addOnSuccessListener {

                    }.addOnFailureListener {
                        val data = hashMapOf(
                            "fcmToken" to token,
                            "point" to 0.00,
                            "contribution" to 0.00)
                        db.collection("users").document(userUid)
                            .set(data, SetOptions.merge())
                            .addOnSuccessListener {
                                adminConfiguration("test")
                            }
                            .addOnFailureListener {
                            }
                    }

            })
    }

    //======First sign configuration=====//
    private fun adminConfiguration(text: String): Task<String> {
        // Create the arguments to the callable function.
        val data = hashMapOf(
            "text" to text,
            "push" to true
        )

        return functions
            .getHttpsCallable("createUser")
            .call(data)
            .continueWith { task ->
                // This continuation runs on either success or failure, but if the task
                // has failed then result will throw an Exception which will be
                // propagated down.
                val result = task.result?.data as String
                result
            }
    }

    companion object {
        private const val TAG = "MainActivity"
        private val REQUEST_PERMISSIONS_REQUEST_CODE = 34
        private const val PRIVACY_POLICY = "https://www.kamira.energy/privacy-policy.html"
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

    private var customTabHelper: CustomTabHelper = CustomTabHelper()
    override fun openPolicyPrivacy() {
        val builder = CustomTabsIntent.Builder()

        // modify toolbar color
        builder.setToolbarColor(ContextCompat.getColor(this, R.color.colorPrimary))

        // add share button to overflow menu
        builder.addDefaultShareMenuItem()

        val anotherCustomTab = CustomTabsIntent.Builder().build()

        val requestCode = 100
        val intent = anotherCustomTab.intent
        intent.data = Uri.parse(PRIVACY_POLICY)

        val pendingIntent = PendingIntent.getActivity(this@MainActivity,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT)

        // add menu item to oveflow
        builder.addMenuItem("Sample item", pendingIntent)

        // menu item icon
        // val bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher)
        // builder.setActionButton(bitmap, "Android", pendingIntent, true)

        // modify back button icon
        // builder.setCloseButtonIcon(bitmap)

        // show website title
        builder.setShowTitle(true)

        // animation for enter and exit of tab
        builder.setStartAnimations(this@MainActivity, android.R.anim.fade_in, android.R.anim.fade_out)
        builder.setExitAnimations(this@MainActivity, android.R.anim.fade_in, android.R.anim.fade_out)

        val customTabsIntent = builder.build()

        // check is chrom available
        val packageName = customTabHelper.getPackageNameToUse(this@MainActivity, PRIVACY_POLICY)

        if (packageName == null) {
            // if chrome not available open in web view
            //val intentOpenUri = Intent(this, WebViewActivity::class.java)
            //intentOpenUri.putExtra(WebViewActivity.EXTRA_URL, Uri.parse(PRIVACY_POLICY).toString())
            //startActivity(intentOpenUri)
        } else {
            customTabsIntent.intent.setPackage(packageName)
            customTabsIntent.launchUrl(this@MainActivity, Uri.parse(PRIVACY_POLICY))
        }
    }

    override fun captureBottle(){
        val intent = Intent(this, CapturebottleActivity::class.java)
        startActivity(intent)
    }

}
