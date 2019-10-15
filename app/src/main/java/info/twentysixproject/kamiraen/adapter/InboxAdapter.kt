package info.twentysixproject.kamiraen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.twentysixproject.kamiraen.databinding.ItemsInboxBinding
import info.twentysixproject.kamiraen.dataclass.Inbox

class InboxAdapter(var clickListener: InboxListener) : ListAdapter<Inbox, InboxAdapter.ViewHolder>(InboxDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemsInboxBinding) : RecyclerView.ViewHolder(binding.root){

        fun bind(item: Inbox, clickListener: InboxListener) {
            binding.inbox = item
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemsInboxBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class InboxDiffCallback : DiffUtil.ItemCallback<Inbox>() {

    override fun areItemsTheSame(oldItem: Inbox, newItem: Inbox): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Inbox, newItem: Inbox): Boolean {
        return oldItem == newItem
    }
}

class InboxListener(val clickListener: (data: Inbox) -> Unit) {
    fun onClick(message: Inbox) = clickListener(message)
}
