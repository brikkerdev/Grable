package ru.sirius.grable.progress
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import ru.sirius.grable.R

class FragmentStatisticsBinding(rootView: View) {
    val root: View = rootView
    val spinnerPeriod: Spinner = rootView.findViewById(R.id.spinnerPeriod)
    val tvPeriod: TextView = rootView.findViewById(R.id.tvPeriod)
    val tvLearningNow: TextView = rootView.findViewById(R.id.tvLearningNow)
    val buttonRetry: Button = rootView.findViewById(R.id.buttonRetry)
    val progressBar: ProgressBar = rootView.findViewById(R.id.progressBar)
    val contentLayout: LinearLayout = rootView.findViewById(R.id.contentLayout)
    val errorLayout: LinearLayout = rootView.findViewById(R.id.errorLayout)
    val textError: TextView = rootView.findViewById(R.id.textError)
    val rvDays: RecyclerView = rootView.findViewById(R.id.rvDays)
    val buttonAddWord: Button = rootView.findViewById(R.id.buttonAddWord)
    val buttonMarkKnown: Button = rootView.findViewById(R.id.buttonMarkKnown)
    val buttonAddRepeat: Button = rootView.findViewById(R.id.buttonAddRepeat)
    val emptyChartText: TextView = rootView.findViewById(R.id.emptyChartText)
    val barChart: BarChart = rootView.findViewById(R.id.barChart)
    val rvStatistics: RecyclerView = rootView.findViewById(R.id.rvStatistics)

    companion object {
        fun inflate(inflater: LayoutInflater, container: ViewGroup?, attachToRoot: Boolean): FragmentStatisticsBinding {
            val rootView = inflater.inflate(R.layout.fragment_progress, container, attachToRoot)
            return FragmentStatisticsBinding(rootView)
        }
    }
}