package info.twentysixproject.kamiraen.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import info.twentysixproject.kamiraen.databinding.ItemsCatalogRewardsBinding
import info.twentysixproject.kamiraen.dataclass.Rewards

class RewardsCatalogAdapter(var clickListener: RewardsCatalogListener) : ListAdapter<Rewards, RewardsCatalogAdapter.ViewHolder>(RewardsCatalogDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //holder.bind(getItem(position)!!, clickListener)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder.from(parent)
    }

    class ViewHolder private constructor(val binding: ItemsCatalogRewardsBinding) : RecyclerView.ViewHolder(binding.root){

        // item: Rewards, clickListener: RewardsCatalogListener
        fun bind() {
            //binding.inbox = item
            //binding.clickListener = clickListener
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemsCatalogRewardsBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }
        }
    }
}

class RewardsCatalogDiffCallback : DiffUtil.ItemCallback<Rewards>() {

    override fun areItemsTheSame(oldItem: Rewards, newItem: Rewards): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Rewards, newItem: Rewards): Boolean {
        return oldItem == newItem
    }
}

class RewardsCatalogListener(val clickListener: (data: Rewards) -> Unit) {
    fun onClick(catalog: Rewards) = clickListener(catalog)
}
