package ru.sirius.grable.feature.home.impl.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.feature.home.impl.databinding.ItemHomeActionBinding

class HomeActionsAdapter(
    private val onActionClick: (HomeAction) -> Unit
) : RecyclerView.Adapter<HomeActionsAdapter.ActionViewHolder>() {

    private val actions = mutableListOf<HomeAction>()

    fun submitList(newActions: List<HomeAction>) {
        actions.clear()
        actions.addAll(newActions)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ActionViewHolder {
        val binding = ItemHomeActionBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ActionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ActionViewHolder, position: Int) {
        holder.bind(actions[position])
    }

    override fun getItemCount(): Int = actions.size

    inner class ActionViewHolder(
        private val binding: ItemHomeActionBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(action: HomeAction) {
            binding.actionTitle.text = binding.root.context.getString(action.titleRes)
            binding.actionDesc.text = if (action.badge != null) {
                action.badge
            } else {
                binding.root.context.getString(action.descriptionRes)
            }
            binding.actionButton.text = binding.root.context.getString(action.buttonTextRes)

            binding.actionCard.setOnClickListener {
                onActionClick(action)
            }
            binding.actionButton.setOnClickListener {
                onActionClick(action)
            }
        }
    }
}

