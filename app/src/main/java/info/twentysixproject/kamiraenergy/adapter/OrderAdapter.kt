package info.twentysixproject.kamiraenergy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.twentysixproject.kamiraenergy.databinding.ItemsOrderRequestBinding
import info.twentysixproject.kamiraenergy.dataclass.Orders

class OrderAdapter(var clickListener: OnGoingOrderListener) : ListAdapter<Orders, OrderAdapter.ViewHolder>(OrderDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemsOrderRequestBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Orders, clickListener: OnGoingOrderListener) {
            binding.order = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemsOrderRequestBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class OrderDiffCallback : DiffUtil.ItemCallback<Orders>() {

    override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
        return oldItem == newItem
    }
}

class OnGoingOrderListener(val clickListener: (data: Orders) -> Unit) {
    fun onClick(order: Orders) = clickListener(order)
}
