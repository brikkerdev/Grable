package ru.sirius.grable.add_word

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.R

class ExamplesAdapter(
    private var items: List<String>,
    private val onEditClick: (position: Int, text: String) -> Unit
) : RecyclerView.Adapter<ExamplesAdapter.ExampleViewHolder>() {

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
        val example = items[position]
        val parts = example.split("—")
        holder.tvEn.text = parts.getOrNull(0)?.trim() ?: ""
        holder.tvRu.text = parts.getOrNull(1)?.trim() ?: ""

        holder.btnEdit.setOnClickListener {
            onEditClick(position, example)
        }
    }

    override fun getItemCount(): Int = items.size

    fun updateData(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }
}
