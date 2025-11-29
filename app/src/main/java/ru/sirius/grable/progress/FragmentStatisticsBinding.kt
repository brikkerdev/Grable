package ru.sirius.grable.progress
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import ru.sirius.grable.R

class FragmentStatisticsBinding(rootView: View) {
    val root: View = rootView
    val spinnerPeriod: Spinner = rootView.findViewById(R.id.spinnerPeriod)
    val tvPeriod: TextView = rootView.findViewById(R.id.tvPeriod)
    val barChart: BarChart = rootView.findViewById(R.id.barChart)
    val rvStatistics: RecyclerView = rootView.findViewById(R.id.rvStatistics)

    companion object {
        fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): FragmentStatisticsBinding {
            val rootView = inflater.inflate(R.layout.fragment_progress, container, attachToRoot)
            return FragmentStatisticsBinding(rootView)
        }
    }
}