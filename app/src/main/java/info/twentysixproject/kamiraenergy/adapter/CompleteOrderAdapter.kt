package info.twentysixproject.kamiraenergy.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.twentysixproject.kamiraenergy.databinding.ItemsOrderCompleteBinding
import info.twentysixproject.kamiraenergy.dataclass.Orders

class CompleteOrderAdapter() : ListAdapter<Orders, CompleteOrderAdapter.ViewHolder>(CompleteOrderDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemsOrderCompleteBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Orders) {
            binding.order = item
            //binding.clickListener = clickListener // Not yet have listener here
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemsOrderCompleteBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class CompleteOrderDiffCallback : DiffUtil.ItemCallback<Orders>() {

    override fun areItemsTheSame(oldItem: Orders, newItem: Orders): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Orders, newItem: Orders): Boolean {
        return oldItem == newItem
    }
}

class CompleteOrderListener(val clickListener: (id: String) -> Unit) {
    fun onClick(order: Orders) = clickListener(order.id)
}