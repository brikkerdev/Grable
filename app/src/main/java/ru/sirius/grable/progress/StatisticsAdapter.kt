package ru.sirius.grable.progress

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.sirius.grable.progress.data.StatisticItem
import ru.sirius.grable.R

class StatisticsAdapter(private val statistics: List<StatisticItem>) :
    RecyclerView.Adapter<StatisticsAdapter.StatisticViewHolder>() {

    class StatisticViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTitle: TextView = itemView.findViewById(R.id.tvTitle)
        private val tvTotal: TextView = itemView.findViewById(R.id.tvTotal)
        private val tvPeriod: TextView = itemView.findViewById(R.id.tvPeriod)

        fun bind(stat: StatisticItem) {
            tvTitle.text = stat.title
            tvTotal.text = stat.total.toString()
            tvPeriod.text = stat.period.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stat, parent, false)
        return StatisticViewHolder(view)
    }

    override fun onBindViewHolder(holder: StatisticViewHolder, position: Int) {
        holder.bind(statistics[position])
    }

    override fun getItemCount() = statistics.size
}