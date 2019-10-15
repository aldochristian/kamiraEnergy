package info.twentysixproject.kamiraen.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import info.twentysixproject.kamiraen.dataclass.Orders
import info.twentysixproject.kamiraen.utils.Utils

class CompletedOrderViewModel : ViewModel() {

    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    var completeOrderList= MutableLiveData<List<Orders>>()

    fun getCompleteOrder() : LiveData<List<Orders>> {
        return completeOrderList
    }

    fun fetchFirestore(){

        val orderList : MutableList<Orders> = mutableListOf()
        db.collection("users").document(user!!.uid)
            .collection("myOrders").orderBy("dateClosed", Query.Direction.DESCENDING).limit(10)
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    //var orderItem = document.toObject(Orders::class.java)
                    orderList.add(Orders(
                        document.id,
                        document.get("status").toString(),
                        document.get("dateCreated").toString(),
                        null,
                        document.get("address").toString(),
                        document.get("collectorName").toString(),
                        document.get("wasteType").toString(),
                        document.get("weight").toString(),
                        Utils.convertTimeFromFirebase(document.get("dateClosed") as Timestamp))
                        )

                    //Log.d(TAG, "${document.id} => ${document.data}")
                }
                completeOrderList.value = orderList
            }
            .addOnFailureListener{
            }

    }
}
