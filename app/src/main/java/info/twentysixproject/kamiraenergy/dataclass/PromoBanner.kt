package info.twentysixproject.kamiraenergy.dataclass

import com.google.firebase.database.Exclude
import com.google.firebase.database.IgnoreExtraProperties

@IgnoreExtraProperties
data class PromoBanner(
    var uid: String? = "", // Promotion_01
    var img: String? = "", // Img path
    var name: String? = "", // Title to be showed
    var remark: String? = "" // remark
)