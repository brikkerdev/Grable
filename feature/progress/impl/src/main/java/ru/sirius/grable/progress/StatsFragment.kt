package ru.sirius.grable.progress

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import ru.sirius.grable.progress.data.DayStat
import ru.sirius.grable.progress.data.StatisticsData
import ru.sirius.grable.core.design.R
import ru.sirius.feature.progress.impl.R as ProgressR

class StatsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!

    // Используем Koin для получения ViewModel
    private val viewModel: StatisticsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupObservers()
        loadInitialData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupViews() {
        setupSpinner()
        setupChart()
        setupRecyclerViews()
        setupMockDataButton()
        setupErrorHandling()
    }

    private fun setupObservers() {
        // Наблюдаем за статистикой
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.statisticsData.collect { data ->
                updateUI(data)
                updateChart(data.days)
            }
        }

        // Наблюдаем за количеством изучаемых слов
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.learningWordsCount.collect { count ->
                binding.tvLearningNow.text = getString(ProgressR.string.stats_learning_now, count)
            }
        }

        // Наблюдаем за состоянием загрузки
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isLoading.collect { isLoading ->
                if (isLoading) {
                    showLoading()
                } else {
                    hideLoading()
                }
            }
        }

        // Наблюдаем за ошибками
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.errorMessage.collect { error ->
                error?.let {
                    showError(it)
                } ?: hideError()
            }
        }
    }

    private fun setupErrorHandling() {
        binding.buttonRetry.setOnClickListener {
            viewModel.clearError()
            viewModel.refreshData()
        }
    }

    private fun showLoading() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.errorLayout.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.progressBar.visibility = View.GONE
        binding.contentLayout.visibility = View.VISIBLE
    }

    private fun showError(message: String) {
        binding.errorLayout.visibility = View.VISIBLE
        binding.contentLayout.visibility = View.GONE
        binding.textError.text = message
    }

    private fun hideError() {
        binding.errorLayout.visibility = View.GONE
    }

    private fun loadInitialData() {
        // Начальная загрузка уже происходит через ViewModel
    }

    private fun setupSpinner() {
        val periods = arrayOf("7 дней", "30 дней", "90 дней", "Все время")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, periods)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPeriod.adapter = adapter

        binding.spinnerPeriod.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val period = when (position) {
                    0 -> 7
                    1 -> 30
                    2 -> 90
                    else -> -1 // Все время
                }
                viewModel.setPeriod(period)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun setupChart() {
        binding.barChart.apply {
            // Базовая настройка графика
            setDrawBarShadow(false)
            setDrawValueAboveBar(true)
            description.isEnabled = false
            setMaxVisibleValueCount(60)
            setPinchZoom(false)
            setDrawGridBackground(false)

            // Настройка оси X
            val xAxis = xAxis
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.setDrawGridLines(false)
            xAxis.granularity = 1f
            xAxis.labelCount = 7
            xAxis.valueFormatter = IndexAxisValueFormatter()

            // Настройка левой оси Y
            val leftAxis = axisLeft
            leftAxis.setDrawGridLines(true)
            leftAxis.spaceTop = 15f
            leftAxis.axisMinimum = 0f
            leftAxis.granularity = 1f
            leftAxis.textColor = ContextCompat.getColor(requireContext(), R.color.primary)

            // Настройка правой оси Y
            val rightAxis = axisRight
            rightAxis.setDrawGridLines(false)
            rightAxis.axisMinimum = 0f
            rightAxis.textColor = ContextCompat.getColor(requireContext(), R.color.primary)

            // Легенда
            val legend = legend
            legend.isEnabled = false

            // Анимация
            animateY(1000)
        }
    }

    private fun setupRecyclerViews() {
        binding.rvDays.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvStatistics.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupMockDataButton() {
        binding.buttonGenerateMockData.setOnClickListener {
            viewModel.generateMockData()
        }
    }

    private fun updateUI(data: StatisticsData) {
        binding.tvPeriod.text = getString(ProgressR.string.stats_period, data.periodTitle)

        // Обновляем адаптеры
        binding.rvDays.adapter = DaysAdapter(data.days)
        binding.rvStatistics.adapter = StatisticsAdapter(data.statistics)
    }

    private fun updateChart(days: List<DayStat>) {
        if (days.isEmpty()) {
            binding.barChart.visibility = View.GONE
            binding.emptyChartText.visibility = View.VISIBLE
            binding.tvDaysTitle.visibility = View.GONE
            binding.rvDays.visibility = View.GONE
            binding.divider.visibility = View.GONE
            return
        }

        binding.barChart.visibility = View.VISIBLE
        binding.emptyChartText.visibility = View.GONE
        binding.tvDaysTitle.visibility = View.VISIBLE
        binding.rvDays.visibility = View.VISIBLE
        binding.divider.visibility = View.VISIBLE

        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        // Ограничиваем количество отображаемых дней для лучшей читаемости
        val displayDays = if (days.size > 10) days.takeLast(10) else days

        displayDays.forEachIndexed { index, dayStat ->
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
        binding.barChart.xAxis.labelCount = minOf(labels.size, 7)
        binding.barChart.animateY(1000)
        binding.barChart.invalidate()
    }
}