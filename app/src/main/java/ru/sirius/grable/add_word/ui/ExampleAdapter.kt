package ru.sirius.grable.add_word.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R

class ExampleAdapter(
    private val onEditClick: (position: Int, example: Example) -> Unit
) : ListAdapter<Example, ExampleAdapter.ExampleViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Example>() {
            override fun areItemsTheSame(oldItem: Example, newItem: Example): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Example, newItem: Example): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class ExampleViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvEn: TextView = view.findViewById(R.id.tvExampleEn)
        val tvRu: TextView = view.findViewById(R.id.tvExampleRu)
        val btnEdit: ImageView = view.findViewById(R.id.btnEditExample)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExampleViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_example, parent, false)
        return ExampleViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExampleViewHolder, position: Int) {
        val example = getItem(position)
        holder.tvEn.text = example.english
        holder.tvRu.text = example.russian

        holder.btnEdit.setOnClickListener {
            onEditClick(position, example)
        }
    }
}