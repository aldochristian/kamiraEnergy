package info.twentysixproject.kamiraenergy.viewmodel

import android.location.Location
import android.location.LocationManager
import androidx.lifecycle.ViewModel
import com.google.type.LatLng
import info.twentysixproject.kamiraenergy.dataclass.Orders

class OrderViewModel: ViewModel() {

    var orderTemp = mutableListOf<Orders>()

    fun addData(){
        val location = Location(LocationManager.GPS_PROVIDER)
        //orderTemp.add(Orders("10","Completed","Jan 10 2019 1.00 pm",location,"Ar", 30.5))
        //orderTemp.add(Orders("11","Requested","Jan 13 2019 1.00 pm",location,"Ar", 0.0))
    }
}