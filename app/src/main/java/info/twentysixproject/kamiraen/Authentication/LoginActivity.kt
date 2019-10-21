
package info.twentysixproject.kamiraen.Authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import info.twentysixproject.kamiraen.Main.MainActivity
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.utils.Utils.openCustomeLink

class LoginActivity : AppCompatActivity(),
    LoginFragment.OnFragmentInteractionForLogin,
    CreateProfileFragment.OnFragmentInteractionForCreateProfile{

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Navigation controller
        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.authentication_navgs) as NavHostFragment? ?: return
        val navController = host.navController
    }

    override fun onBackPressed() {
        super.onBackPressed()
        auth.signOut()
        finish()
    }

    override fun callMainActivity(firstTimeLogin: Boolean) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("firstTimeLogin", firstTimeLogin)
        startActivity(intent)
        finish()
    }

    override fun privacyPolicy() {
        openCustomeLink(this@LoginActivity, "https://www.kamira.energy/privacy-policy.html")
    }

}
