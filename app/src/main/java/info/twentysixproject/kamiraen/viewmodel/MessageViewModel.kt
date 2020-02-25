package info.twentysixproject.kamiraen.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import info.twentysixproject.kamiraen.database.InboxRepository
import info.twentysixproject.kamiraen.database.InboxRoomDatabase
import info.twentysixproject.kamiraen.dataclass.Inbox
import info.twentysixproject.kamiraen.utils.Utils
import kotlinx.coroutines.launch

class MessageViewModel (application: Application) : AndroidViewModel(application){

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    /**
     * Testing function for room
     */
    var messageList = MutableLiveData<List<Inbox>>()

    private val repository: InboxRepository
    val allMessages: LiveData<List<Inbox>>

    init {
        val inboxDao = InboxRoomDatabase.getDatabase(application, viewModelScope).inboxDao()
        repository = InboxRepository(inboxDao)
        allMessages = repository.allMessages
    }

    fun insert(inbox: Inbox) = viewModelScope.launch {
        repository.insert(inbox)
    }
    /** END OF TESTING FUNCTION **/

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