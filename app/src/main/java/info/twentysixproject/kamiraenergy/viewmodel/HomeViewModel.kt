package info.twentysixproject.kamiraenergy.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import info.twentysixproject.kamiraenergy.model.SlideModel
import com.google.firebase.database.DatabaseReference
import androidx.lifecycle.LiveData
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestoreException
import info.twentysixproject.kamiraenergy.utils.Utils


class HomeViewModel: ViewModel(){

    private lateinit var promoReference: DatabaseReference
    val storage = FirebaseStorage.getInstance("gs://twentysixproject-a4530")
    val db = FirebaseFirestore.getInstance()
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user: FirebaseUser? = auth.currentUser

    private val _mypoints = MutableLiveData<String>()
    val myPoints: LiveData<String>
        get() = _mypoints

    private val _mycontribution = MutableLiveData<String>()
    val myContribution: LiveData<String>
        get() = _mycontribution

    init {
        getProfileUserFirestore()
    }

    fun loadImageSlider(): ArrayList<SlideModel> {

        //Image Slider management
        val imageList = ArrayList<SlideModel>()
        //imageList.add(SlideModel("https://1.bp.blogspot.com/-GUZsgr8my50/XJUWOhyHyaI/AAAAAAAABUo/bljp3LCS3SUtj-judzlntiETt7G294WcgCLcBGAs/s1600/fox.jpg", "Foxes live wild in the city.", true))
        //imageList.add(SlideModel("https://2.bp.blogspot.com/-CyLH9NnPoAo/XJUWK2UHiMI/AAAAAAAABUk/D8XMUIGhDbwEhC29dQb-7gfYb16GysaQgCLcBGAs/s1600/tiger.jpg"))
        //imageList.add(SlideModel("https://3.bp.blogspot.com/-uJtCbNrBzEc/XJUWQPOSrfI/AAAAAAAABUs/ZlReSwpfI3Ack60629Rv0N8hSrPFHb3TACLcBGAs/s1600/elephant.jpg", "The population of elephants is decreasing in the world."))

        imageList.add(SlideModel(
            "https://firebasestorage.googleapis.com/v0/b/twentysixproject-a4530/o/promo%2Fjaga_botol_tidak_ke_laut.png?alt=media&token=7de26d54-1990-4133-8ce1-c43eb43c9569"))
        return imageList
    }

    fun getProfileUserFirestore(){
        db.collection("users")
            .document(user!!.uid)
            .get()
            .addOnSuccessListener { document ->
                _mypoints.value = document.get("point").toString()
                _mycontribution.value = document.get("contribution").toString()

            }.addOnFailureListener {

            }
    }

    fun activatedCode(code: String): Boolean{
        val redeemCodeDoc = db.collection("redeemcode").document(code)
        var codeFoundStatus = false
        val contribution = 0.00
        var pointToBeAdded = 0.00

        db.runTransaction {
            val snapshot = it.get(redeemCodeDoc)
            val activatedBy = snapshot.get("activatedBy").toString()
            //val contribution = snapshot.get("contribution") as Double
            pointToBeAdded = snapshot.get("pointcount") as Double
            if (activatedBy == "blank") {
                it.update(redeemCodeDoc, "activatedBy", user?.uid)
            } else {
                throw FirebaseFirestoreException(
                    "code has activated",
                    FirebaseFirestoreException.Code.ABORTED
                )
            }
        }.addOnSuccessListener {
            givePointsandCode(code, pointToBeAdded, contribution)
            codeFoundStatus = true
        }.addOnFailureListener {
            codeFoundStatus = false
        }

        return codeFoundStatus
    }

    fun givePointsandCode(code: String, points: Double, contribution: Double){
        val data = hashMapOf(
           "codeReward" to code,
            "addedBy" to "Rewards bot",
            "dateCreated" to FieldValue.serverTimestamp(),
            "amount" to points,
            "contribution" to contribution
        )

        db.collection("users").document(user!!.uid)
            .collection("pointHistory")
            .add(data)
            .addOnSuccessListener { _ ->
                transactionPointContribution(points, contribution)
            }
            .addOnFailureListener { _ ->
            }
    }

    fun transactionPointContribution(points: Double, contribution: Double){

        val sfDocRef = db.collection("users").document(user!!.uid)

        db.runTransaction { transaction ->
            val snapshot = transaction.get(sfDocRef)

            val newPoint = snapshot.getDouble("point")!! + points
            val newContribution = snapshot.getDouble("contribution")!! + contribution
            transaction.update(sfDocRef, "point", newPoint)
            transaction.update(sfDocRef, "contribution", newContribution)

            null
        }.addOnSuccessListener {  }
            .addOnFailureListener { _ ->  }

    }

    fun welcomeMessage(){
        val msg = hashMapOf(
            "categorize" to "Welcome message",
            "dateCreated" to FieldValue.serverTimestamp(),
            "dateValid" to Utils.convertStringtoDate("Sep 27, 2019"),
            "header" to "Welcome to kamira",
            "img" to null
        )

        db.collection("users").document("inbox")
            .set(msg)
            .addOnSuccessListener {  }
            .addOnFailureListener { _ ->  }
    }

    fun limitedOrder(){
        /**
         * This function purpose is to limit only 3 - 5 order for user not yet verified / activated
         * install observer untuk check counter
         */
    }

}