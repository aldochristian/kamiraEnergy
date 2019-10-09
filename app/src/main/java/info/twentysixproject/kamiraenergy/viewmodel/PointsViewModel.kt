package info.twentysixproject.kamiraenergy.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class PointsViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    private val _mypoints = MutableLiveData<String>()
    val myPoints: LiveData<String>
        get() = _mypoints

    init {
        fetchFirestore()
    }

    fun fetchFirestore(){
        val docRef = db.collection("users").document(user!!.uid)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    _mypoints.value = document.get("point").toString()
                } else {
                    _mypoints.value = "0"
                }
            }
            .addOnFailureListener { _ ->
                //Log.d(TAG, "get failed with ", exception)
            }
    }
}