package info.twentysixproject.kamiraen.adapter

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import info.twentysixproject.kamiraen.R
import info.twentysixproject.kamiraen.dataclass.Orders

/*@BindingAdapter("statusIcon")
fun ImageView.setImage(item: Orders?) {
    item?.let {
        setImageResource(when (item.status) {
            "Completed" -> R.drawable.ic_cloud_trash
            else -> R.drawable.ic_cancel
        })
    }
}*/

object Bindingutils {

    @JvmStatic
    @BindingAdapter("statusIcon")
    fun ImageView.setSleepImage(item: Orders?) {
        setImageResource(when (item?.status) {
            "Completed" -> R.drawable.ic_cloud_trash
            else -> R.drawable.ic_cancel
        })
    }
}