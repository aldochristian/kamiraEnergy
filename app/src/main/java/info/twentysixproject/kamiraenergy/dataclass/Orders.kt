package info.twentysixproject.kamiraenergy.dataclass

import com.google.firebase.firestore.GeoPoint
import com.google.gson.annotations.SerializedName

data class Orders (
    val id: String,
    @SerializedName("status") val status: String,
    @SerializedName("dateCreated") val dateCreated: String?,
    @SerializedName("loc") val loc: GeoPoint?,
    @SerializedName("address") val address: String?,
    @SerializedName("collectorName") val collectorName: String?,
    @SerializedName("wasteType")val wasteType: String?,
    @SerializedName("weight")val weight: String?,
    val dateClosed: String?

)