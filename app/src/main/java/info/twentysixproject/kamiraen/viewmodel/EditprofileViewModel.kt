package info.twentysixproject.kamiraen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class EditprofileViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    private val _hasComplete = MutableLiveData<Boolean>()
    val hasComplete: LiveData<Boolean>
        get() = _hasComplete

    private val _hasLoaded = MutableLiveData<Boolean>()
    val hasLoaded: LiveData<Boolean>
        get() = _hasLoaded

    /**
     * Activated status is have three status. Each status has limited access
     * registered, activated, verified
     */
    private val _activatedAccount = MutableLiveData<String>()
    val activatedAccount: LiveData<String>
        get() = _activatedAccount

    val name = MutableLiveData<String>()
    val city = MutableLiveData<String>()
    val zipcode = MutableLiveData<String>()
    val sex_male = MutableLiveData<Boolean>()
    val sex_female = MutableLiveData<Boolean>()
    val sex = MutableLiveData<String>()
    val phone = MutableLiveData<String>()
    val address = MutableLiveData<String>()

    init {
        fetchFirestore()
    }

    private fun fetchFirestore(){
        _hasLoaded.value = false
        val docRef = db.collection(USERS).document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    //Log.d(TAG, "DocumentSnapshot data: ${document.data}")
                    name.value = document.get(FULLNAME).toString()
                    val sex = document.get(SEX).toString()
                    if(sex == "male"){
                        sex_male.value = true
                    }else if (sex == "female"){
                        sex_female.value = true
                    }

                    city.value = document.get(CITY).toString()
                    zipcode.value = document.get(ZIPCODE).toString()

                    _activatedAccount.value = document.get("activeStatus").toString()
                } else {
                    name.value = null
                    city.value = null
                    zipcode.value = null
                }
            }
            .addOnFailureListener {
            }
        phone.value = "You had registered with number "+user.phoneNumber
        _hasLoaded.value = true
        _hasComplete.value = false
    }

    fun onClickSave(){
        storeFirestore()
    }

    fun storeFirestore(){
        if(sex_male.value == true){
            sex.value = "male"
        }else if (sex_female.value == true){
            sex.value = "female"
        }else{
            sex.value = ""
        }

        val docRef = db.collection(USERS).document(user!!.uid)
        docRef.update(mapOf(
            FULLNAME to name.value,
            SEX to sex.value,
            ADDRESS to address.value,
            CITY to city.value,
            ZIPCODE to zipcode.value
        )).addOnSuccessListener {
                _hasComplete.value = true
        }.addOnFailureListener{}
    }

    companion object{
        //Firebase coll and docs
        val USERS:String = "users"
        val SEX: String = "sex"
        val FULLNAME:String = "fullname"
        val CITY:String = "city"
        val ZIPCODE:String = "zipcode"
        val ADDRESS:String = "address"
    }
}
