package info.twentysixproject.kamiraenergy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import info.twentysixproject.kamiraenergy.dataclass.Inbox
import info.twentysixproject.kamiraenergy.utils.Utils

class MessageViewModel : ViewModel(){

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    var messageList = MutableLiveData<List<Inbox>>()

    fun fetchFirestore(){

        val inboxList : MutableList<Inbox> = mutableListOf()
        db.collection("users").document(user!!.uid)
            .collection("inbox")
            .orderBy("dateCreated", Query.Direction.DESCENDING).limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //var orderItem = document.toObject(Orders::class.java)
                    inboxList.add(Inbox(
                        document.id,
                        document.get("categorize").toString(),
                        Utils.convertTimeFromFirebase(document.get("dateCreated") as Timestamp),
                        Utils.convertTimeFromFirebase(document.get("dateValid") as Timestamp),
                        document.get("header").toString(),
                        document.get("img").toString())
                    )

                    //Log.d(TAG, "${document.id} => ${document.data}")
                }
                messageList.value = inboxList
            }
            .addOnFailureListener{ _ ->
                //Log.w(TAG, "Error getting documents: ", exception)
            }
    }

}