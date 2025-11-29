package ru.sirius.grable.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import ru.sirius.grable.R
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticsData
import ru.sirius.grable.progress.data.repository.StatisticsRepository


class StatsFragment : Fragment() {

    private lateinit var barChart: BarChart
    private lateinit var spinnerPeriod: Spinner
    private lateinit var tvPeriod: TextView
    private lateinit var rvDays: RecyclerView
    private lateinit var rvStatistics: RecyclerView

    private var currentPeriod = 7

    private lateinit var binding: FragmentStatisticsBinding

    private val viewModel by viewModels<StatisticsViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_progress, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews(view)
        setupSpinner()

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statisticsData.collect(::updateUI)
        }
    }

    private fun initViews(view: View) {
        barChart = view.findViewById(R.id.barChart)
        spinnerPeriod = view.findViewById(R.id.spinnerPeriod)
        tvPeriod = view.findViewById(R.id.tvPeriod)
        rvDays = view.findViewById(R.id.rvDays)
        rvStatistics = view.findViewById(R.id.rvStatistics)

        rvDays.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        rvStatistics.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupSpinner() {
        val periods = arrayOf("7 дней", "30 дней", "90 дней", "Все время")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerPeriod.adapter = adapter

        spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                currentPeriod = when (position) {
                    0 -> 7
                    1 -> 30
                    2 -> 90
                    else -> -1 // Все время
                }
                viewModel.setPeriod(currentPeriod)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateUI(data: StatisticsData) {
        tvPeriod.text = "Период: ${data.periodTitle}"

        // Обновляем заголовок "Заучивается сейчас"
        val learningNowText = "Заучивается сейчас: ${data.learningNow}"
        requireView().findViewById<TextView>(R.id.tvPeriodTitle).text = learningNowText

        // Настраиваем адаптеры
        val daysAdapter = DaysAdapter(data.days)
        rvDays.adapter = daysAdapter

        val statisticsAdapter = StatisticsAdapter(data.statistics)
        rvStatistics.adapter = statisticsAdapter
    }

    private fun updateChart(days: List<DayStat>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        days.forEachIndexed { index, dayStat ->
            entries.add(BarEntry(index.toFloat(), dayStat.wordsCount.toFloat()))
            labels.add(dayStat.date)
        }

        val dataSet = BarDataSet(entries, "Количество слов")
        dataSet.color = ContextCompat.getColor(requireContext(), R.color.primary)
        dataSet.valueTextSize = 10f

        val data = BarData(dataSet)
        data.barWidth = 0.7f

        binding.barChart.data = data
        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        binding.barChart.invalidate()
    }

    private fun setupChart() {
        // Базовая настройка графика
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setMaxVisibleValueCount(60)
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)

        // Настройка оси X
        val xAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.granularity = 1f
        xAxis.labelCount = 7
        xAxis.valueFormatter = IndexAxisValueFormatter()

        // Настройка левой оси Y
        val leftAxis = barChart.axisLeft
        leftAxis.setDrawGridLines(true)
        leftAxis.spaceTop = 15f
        leftAxis.axisMinimum = 0f
        leftAxis.granularity = 1f

        // Настройка правой оси Y
        val rightAxis = barChart.axisRight
        rightAxis.setDrawGridLines(false)
        rightAxis.axisMinimum = 0f

        // Легенда
        val legend = barChart.legend
        legend.isEnabled = false

        // Анимация
        barChart.animateY(1000)
    }
}