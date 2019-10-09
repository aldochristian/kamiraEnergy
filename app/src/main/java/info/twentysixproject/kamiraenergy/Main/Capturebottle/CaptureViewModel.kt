package info.twentysixproject.kamiraenergy.Main.Capturebottle

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class CaptureViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()

    val SUCCESS:Int = 1
    val FAIL:Int = 0
    val NOT_YET_STORED:Int = 2

    private val _imagecapture = MutableLiveData<Uri>()
    val imagecapture: LiveData<Uri>
        get() = _imagecapture

    private val _successUpload = MutableLiveData<Int>()
    val successUpload: LiveData<Int>
        get() = _successUpload

    fun successUploaded(){
        _successUpload.value = NOT_YET_STORED
    }

    fun setImageUri(uri: Uri){
        _imagecapture.value = uri
    }

    fun detailFileToBeUploaded(userId: String?): String{
        val ref = db.collection("capture").document()

        val data = hashMapOf(
            "counterBottle" to 0.00,
            "location" to "",
            "status" to "review",
            "user" to userId
        )

        ref.set(data)
            .addOnSuccessListener {
                incrementCounter(userId)
            }.addOnFailureListener{
                _successUpload.value = FAIL
                return@addOnFailureListener
            }
        return ref.id
    }

    fun incrementCounter(userId: String?){
        val counterRef = db.collection("users").document(userId!!).collection("adminSet").document("counterCapture")

        db.runTransaction { transaction ->
            val snapshot = transaction.get(counterRef)

            val newCounter = snapshot.getDouble("counter")!! + 1.00
            if (newCounter == 3.00){
                transaction.update(counterRef, "banned", true)
            }
            transaction.update(counterRef, "counter", newCounter)

            // Success
            null
        }.addOnSuccessListener {
            _successUpload.value = SUCCESS
        }.addOnFailureListener { _ ->
            _successUpload.value = FAIL
        }
    }
}
