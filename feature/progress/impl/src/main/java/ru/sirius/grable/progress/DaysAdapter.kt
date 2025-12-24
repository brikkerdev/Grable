package ru.sirius.grable.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.feature.progress.impl.R

class DaysAdapter(private val days: List<DayStat>) :
    RecyclerView.Adapter<DaysAdapter.DayViewHolder>() {

    class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val bar: View = itemView.findViewById(R.id.vBar)
        private val tvWordsCount: TextView = itemView.findViewById(R.id.tvWordsCount)
        private val tvDate: TextView = itemView.findViewById(R.id.tvDate)

        fun bind(day: DayStat, maxWords: Int) {
            tvDate.text = day.date
            tvWordsCount.text = day.wordsCount.toString()

            // Динамическая высота столбца
            val layoutParams = bar.layoutParams
            if (maxWords > 0) {
                val percentage = day.wordsCount.toFloat() / maxWords
                layoutParams.height = (percentage * 80).toInt()
            } else {
                layoutParams.height = 0
            }
            bar.layoutParams = layoutParams
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DayViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_day_stat, parent, false)
        return DayViewHolder(view)
    }

    override fun onBindViewHolder(holder: DayViewHolder, position: Int) {
        val day = days[position]
        val maxWords = days.maxOfOrNull { it.wordsCount } ?: 0
        holder.bind(day, maxWords)
    }

    override fun getItemCount() = days.size
}