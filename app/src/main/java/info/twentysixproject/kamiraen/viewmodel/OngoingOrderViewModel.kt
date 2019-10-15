package info.twentysixproject.kamiraen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import info.twentysixproject.kamiraen.dataclass.Orders
import info.twentysixproject.kamiraen.utils.Utils


class OngoingOrderViewModel : ViewModel() {

    val TAG:String = "OngoingOrderViewModel"

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    var onGoingOrderList= MutableLiveData<List<Orders>>()
    var dataToBeCancel: Orders? = null

    private val _navigateToItem = MutableLiveData<String>() // Helper var to check value from list
    val navigateToItem
        get() = _navigateToItem


    fun onCancelClicked(data: Orders) {
        _navigateToItem.value = data.id
        dataToBeCancel = data
    }

    fun getOnGoingOrder(): LiveData<List<Orders>> {
        return onGoingOrderList
    }

    fun updateToCancel(id: String){
        if (dataToBeCancel?.id == id){
            val batch = db.batch()

            val docOrder = db.collection("orders").document(id)
            batch.update(docOrder, "status", "Cancel")
            val docUserRef = db.collection("users").document(user!!.uid).collection("myOrders").document(id)
            batch.set(docUserRef, Orders(
                id,
                dataToBeCancel!!.status,
                dataToBeCancel!!.dateCreated,
                null,
                dataToBeCancel!!.address,
                dataToBeCancel!!.collectorName,
                dataToBeCancel!!.wasteType,
                dataToBeCancel!!.weight,
                FieldValue.serverTimestamp().toString()
            ))
            batch.update(docUserRef, "status", "Cancel")
            batch.update(docUserRef, "dateClosed", FieldValue.serverTimestamp())

            batch.commit().addOnCompleteListener {
                fetchFirestore()
            }
        }else{
        }

    }

    fun fetchFirestore(){

        val orderList : MutableList<Orders> = mutableListOf()
        db.collection("orders")
            .whereEqualTo("user", user?.uid).whereEqualTo("status", "OnGoing")
            .orderBy("dateCreated", Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //var orderItem = document.toObject(Orders::class.java)
                    orderList.add(Orders(
                        document.id,
                        document.get("status").toString(),
                        Utils.convertTimeFromFirebase(document.get("dateCreated") as Timestamp),
                        null,
                        document.get("address").toString(),
                        document.get("collectorName").toString(),
                        document.get("wasteType").toString(),
                        document.get("weight").toString(),
                        document.get("dateClosed").toString()))

                    //Log.d(TAG, "${document.id} => ${document.data}")
                }
                onGoingOrderList.value = orderList
            }
            .addOnFailureListener{ _ ->
            }

    }
}
