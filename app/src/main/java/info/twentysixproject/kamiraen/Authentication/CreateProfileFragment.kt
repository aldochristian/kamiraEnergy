package info.twentysixproject.kamiraen.Authentication


import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.utils.Utils
import kotlinx.android.synthetic.main.fragment_create_profile.*
import kotlinx.android.synthetic.main.fragment_create_profile.view.*

class CreateProfileFragment : Fragment() {

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser
    internal var forCreateProfile: OnFragmentInteractionForCreateProfile? = null
    var validationForm = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if(activity is AppCompatActivity){
            (activity as AppCompatActivity).setSupportActionBar(view.findViewById(R.id.toolbar))
            (activity as AppCompatActivity).supportActionBar?.title = "Profile"
        }


        verification(view) //check first time login

        view.edprofile_btnsave.setOnClickListener {
            view.findViewById<CoordinatorLayout>(R.id.frcreate_progress).visibility = View.VISIBLE
            validation(view)
            if (validationForm){
                createProfile()
            }else{
                view.findViewById<CoordinatorLayout>(R.id.frcreate_progress).visibility = View.GONE
            }
        }
    }

    fun createProfile(){
        var sex: String? = null
            if(male.isChecked == true){
                sex = "male"
            }else if (female.isChecked == true){
                sex = "female"
            }
        val data = hashMapOf(
            FULLNAME to edprofile_txtname.text.toString(),
            PHONE to edprofile_phone.text.toString(),
            SEX to sex,
            ADDRESS to edprofile_address.text.toString(),
            CITY to edprofile_city.text.toString(),
            ZIPCODE to edprofile_zipcode.text.toString()
        )

            db.collection(USERS).document(user!!.uid).set(data)
                .addOnSuccessListener {
                Utils.confirmDialog(
                    requireContext(),
                    "Success to created",
                    "Your profile is ready"
                )
                forCreateProfile?.callMainActivity(true)
            }.addOnFailureListener{
                Utils.warningDialog(
                    requireContext(),
                    "Error",
                    "There is some issue please try again, if issue keep persist kindly connect Kamira admin"
                )
            }

    }

    fun validation(view: View){
        val errorTxt = view.findViewById<TextView>(R.id.edprofile_error)
        errorTxt.text = ""

        if(edprofile_txtname.text.isNullOrEmpty()){
            errorTxt.text = "Complete your name"
            validationForm = false
        }else if(edprofile_phone.text.isNullOrEmpty()){
            errorTxt.text = "Fill your phone number"
            validationForm = false
        }else if (!(view.male.isChecked) && !(view.female.isChecked)){
            errorTxt.text = "Select your gender"
            validationForm = false
        }else if (edprofile_address.text.isNullOrEmpty()){
            errorTxt.text = "Fill your address"
            validationForm = false
        }else if (edprofile_city.text.isNullOrEmpty()){
            errorTxt.text = "Fill with your city name"
            validationForm = false
        }else if (edprofile_zipcode.text.isNullOrEmpty()){
            errorTxt.text = "If you didn't know your code please fill with 0"
            validationForm = false
        }else{
            validationForm = true
        }

    }

    fun verification(view: View){
        val docRef = db.collection("users").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document.data != null) {
                    forCreateProfile?.callMainActivity(false)//Proceed to main
                } else {
                    view.findViewById<CoordinatorLayout>(R.id.frcreate_progress).visibility = View.GONE
                    view.findViewById<LinearLayout>(R.id.frcreate_content).visibility = View.VISIBLE
                }
            }
            .addOnFailureListener {
            }

    }

    override fun onAttach(context: Context)
    {
        super.onAttach(context)

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception.
        if (context is OnFragmentInteractionForCreateProfile) {
            forCreateProfile = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionForEditprofile")
        }
    }

    companion object{
        //Firebase coll and docs
        val USERS:String = "users"
        val SEX: String = "sex"
        val FULLNAME:String = "fullname"
        val PHONE:String = "phone"
        val CITY:String = "city"
        val ZIPCODE:String = "zipcode"
        val ADDRESS:String = "address"
    }

    interface OnFragmentInteractionForCreateProfile{
        fun callMainActivity(firstTimeLogin: Boolean)
    }

}
